package com.ramzi.chunkproject.correct;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.ramzi.chunkproject.*;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.SecureRandom;

public class PlayerCorrectRefrence extends AppCompatActivity implements TaskCallBack{

    public static final String AES_ALGORITHM = "AES";
    public static final String AES_TRANSFORMATION = "AES/CTR/NoPadding";

    private static final String ENCRYPTED_FILE_NAME = "0.part.enc";
    private static final String ENCRYPTED_FILE_NAME2 = "1.part.enc";


    private Cipher mCipher;
    private SecretKeySpec mSecretKeySpec;
    private IvParameterSpec mIvParameterSpec;

    private File mEncryptedFile;
    private File mEncryptedFile2;

    private SimpleExoPlayerView mSimpleExoPlayerView;
    private static int PBKDF2_ITERATIONS = 50000;
    private static int KEY_LENGTH = 256;
    private static int SALT_LENGTH = 64;
    private static int IV_LENGTH = 16;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_main);

        mSimpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.simpleexoplayerview);

        mEncryptedFile = new File(Environment.getExternalStorageDirectory(), ENCRYPTED_FILE_NAME);
        mEncryptedFile2 = new File(Environment.getExternalStorageDirectory(), ENCRYPTED_FILE_NAME2);
        Log.d("URlllll", mEncryptedFile.getAbsolutePath() + ">>>>");

        SecureRandom secureRandom = new SecureRandom();
//    byte[] key = new byte[16];
//    byte[] iv = new byte[16];
        final byte[] iv = {65, 1, 2, 23, 4, 5, 6, 7, 32, 21, 10, 11, 12, 13, 84, 45};
        final byte[] salt = {65, 1, 2, 23, 4, 5, 6, 7, 32, 21, 10, 11, 12, 13, 84, 45};

//      byte[] iv = generateSecureBytes(IV_LENGTH);
//      final byte[] key = { 0, 42, 2, 54, 4, 45, 6, 7, 65, 9, 54, 11, 12, 13, 60, 15, 65, 9, 54, 11, 12, 13, 60, 15 };
//    secureRandom.nextBytes(key);
//    secureRandom.nextBytes(iv);kolmklja
//    byte[] salt = generateSecureBytes(SALT_LENGTH);
        byte[] key = PBKDF2("kolmklja".toCharArray(), salt);
        mSecretKeySpec = new SecretKeySpec(key, AES_ALGORITHM);
        mIvParameterSpec = new IvParameterSpec(iv);

        try {
            mCipher = Cipher.getInstance(AES_TRANSFORMATION);
            mCipher.init(Cipher.DECRYPT_MODE, mSecretKeySpec, mIvParameterSpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        decode();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//    if(mEncryptedFile != null
//            && mEncryptedFile.exists()
//            && mEncryptedFile.length() > 0) {
//      Log.d(getClass().getCanonicalName(),"file deleted");
//      mEncryptedFile.delete();
//    }
    }

    private boolean hasFile() {
        return mEncryptedFile != null
                && mEncryptedFile.exists()
                && mEncryptedFile.length() > 0;
    }

    public void encryptVideo(View view) {
        if (hasFile()) {
            Log.d(getClass().getCanonicalName(), "encrypted file found, no need to recreate");
            return;
        }
        try {
            Cipher encryptionCipher = Cipher.getInstance(AES_TRANSFORMATION);
            encryptionCipher.init(Cipher.ENCRYPT_MODE, mSecretKeySpec, mIvParameterSpec);
            // TODO:
            // you need to encrypt a video somehow with the same key and iv...  you can do that yourself and update
            // the ciphers, key and iv used in this demo, or to see it from top to bottom,
            // supply a url to a remote unencrypted file - this method will download and encrypt it
            // this first argument needs to be that url, not null or empty...
            new DownloadAndEncryptFileTask("https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_10mb.mp4", mEncryptedFile, encryptionCipher).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playVideo(View view) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);

//    TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        mSimpleExoPlayerView.setPlayer(player);
        DataSource.Factory dataSourceFactory = new EncryptedFileDataSourceFactory(mCipher, mSecretKeySpec, mIvParameterSpec, bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        try {
            Log.d("Playing location", "Data" + mEncryptedFile.getAbsolutePath());
            Uri uri = Uri.fromFile(mEncryptedFile);
            Uri uri2 = Uri.fromFile(mEncryptedFile2);
//            DefaultBandwidthMeter  defaultBandwidthMeter = new DefaultBandwidthMeter();
//
//            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this,
//                    Util.getUserAgent(this, "mediaPlayerSample"),defaultBandwidthMeter);
            MediaSource videoSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
            MediaSource videoSource2 = new ExtractorMediaSource(uri2, dataSourceFactory, extractorsFactory, null, null);
//            player.prepare(videoSource);

//            player.prepare(new MergingMediaSource(videoSource,videoSource2));
            MediaSource[] mediaSourcesToLoad = new MediaSource[2];
            mediaSourcesToLoad[0] = videoSource;
            mediaSourcesToLoad[1] = videoSource2;
            ConcatenatingMediaSource mediaSourcess = new ConcatenatingMediaSource(mediaSourcesToLoad);
            player.prepare(mediaSourcess);
            player.setPlayWhenReady(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] PBKDF2(char[] password, byte[] salt) {
        try {
//            [C@17ec0b5
            Log.d("kko", password.length + "");
//        Use PBKDF2WithHmacSHA512 for java 8
//    SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec spec = new PBEKeySpec(password, salt, PBKDF2_ITERATIONS, KEY_LENGTH);
            SecretKey secretKey = secretKeyFactory.generateSecret(spec);

            return secretKey.getEncoded();
        } catch (Exception error) {
            System.out.println("Error: " + error.getMessage());
            return null;
        }
    }

    private static byte[] generateSecureBytes(int size) {
        byte[] secureBytes = new byte[size];
        SecureRandom secRand = new SecureRandom();
        secRand.nextBytes(secureBytes);
        return secureBytes;
    }


    public void decode() {
        String fileName=new File(Environment.getExternalStorageDirectory(), "su.mp4").getAbsolutePath();
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), "su.mp4").getAbsolutePath()+".enc");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        CipherOutputStream cipherOutputStream = new CipherOutputStream(fileOutputStream, mCipher);

        byte buffer[] = new byte[1024 * 1024];
        int bytesRead;
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                Log.d("polakku", "reading from http...");

                cipherOutputStream.write(buffer, 0, bytesRead);

            }

            inputStream.close();
            cipherOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Split(View view) {
//        new SplirMergeTask(0, this, new File(Environment.getExternalStorageDirectory(), "palu.mp4")).execute();
        new SplirMergeTask(1, this, new File(Environment.getExternalStorageDirectory(), "palu.mp4")).execute();

    }

    @Override
    public void taskCallback(boolean isComplete, int type) {

    }
}
