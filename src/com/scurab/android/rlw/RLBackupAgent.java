package com.scurab.android.rlw;

import android.annotation.TargetApi;
import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FullBackupDataOutput;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.*;

/**
 * User: Joe Scurab
 * Date: 13/02/14
 * Time: 21:56
 */
@TargetApi(Build.VERSION_CODES.FROYO)
public class RLBackupAgent extends BackupAgent {

    private static final String KEY = "RLBackupAgent";

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
        // Create buffer stream and data output stream for our data
        ByteArrayOutputStream bufStream = new ByteArrayOutputStream();
        DataOutputStream outWriter = new DataOutputStream(bufStream);
        // Write structured data
        outWriter.writeUTF(DeviceDataProvider.getSerialNumber(getApplicationContext()));
        RemoteLog rl = RemoteLog.getInstance();
        outWriter.writeInt(rl != null ? rl.getDeviceId() : 0);
        // Send the data to the Backup Manager via the BackupDataOutput
        byte[] buffer = bufStream.toByteArray();
        int len = buffer.length;
        data.writeEntityHeader(KEY, len);
        data.writeEntityData(buffer, len);
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode,
                          ParcelFileDescriptor newState) throws IOException {
        String uuid = "";
        int devId = 0;
        // There should be only one entity, but the safest
        // way to consume it is using a while loop
        while (data.readNextHeader()) {
            String key = data.getKey();
            int dataSize = data.getDataSize();

            // If the key is ours (for saving top score). Note this key was used when
            // we wrote the backup entity header
            if (KEY.equals(key)) {
                // Create an input stream for the BackupDataInput
                byte[] dataBuf = new byte[dataSize];
                data.readEntityData(dataBuf, 0, dataSize);
                ByteArrayInputStream baStream = new ByteArrayInputStream(dataBuf);
                DataInputStream in = new DataInputStream(baStream);

                // Read the player name and score from the backup data
                uuid = in.readUTF();
                devId = in.readInt();

                // Record the score on the device (to a file or something)

                Context c = getApplicationContext();
                RemoteLog.onRestoreUUID(c, devId);
                DeviceDataProvider.saveSerialNumber(c, uuid);
            } else {
                // We don't know this entity key. Skip it. (Shouldn't happen.)
                data.skipEntityData();
            }
        }

        // Finally, write to the state blob (newState) that describes the restored data
        FileOutputStream outstream = new FileOutputStream(newState.getFileDescriptor());
        DataOutputStream out = new DataOutputStream(outstream);
        out.writeUTF(uuid);
        out.writeInt(devId);
    }
}
