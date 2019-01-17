package com.ramzi.chunkproject;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.security.SecureRandom;
import java.security.Security;

/**
 * Created by oliveboard on 16/1/19.
 *
 * @auther Ramesh M Nair
 */
public class PlayerActivity extends AppCompatActivity {

    public static final String AES_ALGORITHM = "AES";
    public static final String AES_TRANSFORMATION = "AES/CTR/NoPadding";

    private static final String ENCRYPTED_FILE_NAME = "su.mp4.enc";

//    private Cipher mCipher;
//    private SecretKeySpec mSecretKeySpec;
    private IvParameterSpec mIvParameterSpec;

    private File mEncryptedFile;

    private SimpleExoPlayerView mSimpleExoPlayerView;

    private static int IV_LENGTH = 12;
    private static int SALT_LENGTH = 64;

    private static int PBKDF2_ITERATIONS = 50000;
    private static int KEY_LENGTH = 256;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_main);

        mSimpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.simpleexoplayerview);
        CryptoAES.Encrypt(new File(Environment.getExternalStorageDirectory(), "su.mp4").getAbsolutePath(),"1!asertg7*a".toCharArray());
        mEncryptedFile = new File(Environment.getExternalStorageDirectory(), ENCRYPTED_FILE_NAME);
        Log.d("URlllll",mEncryptedFile.getAbsolutePath()+">>>>");
        if(mEncryptedFile.exists())
        {
            Toast.makeText(getApplicationContext(),"Ya bro file is there",1).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Ya bro file missing is there",1).show();


        }

        /*SecureRandom secureRandom = new SecureRandom();
//    byte[] key = new byte[16];
//    byte[] iv = new byte[16];
        final byte[] iv = { 65, 1, 2, 23, 4, 5, 6, 7, 32, 21, 10, 11, 12, 13, 84, 45 };
        final byte[] key = { 0, 42, 2, 54, 4, 45, 6, 7, 65, 9, 54, 11, 12, 13, 60, 15 };
        secureRandom.nextBytes(key);
        secureRandom.nextBytes(iv);

        mSecretKeySpec = new SecretKeySpec(key, AES_ALGORITHM);
        mIvParameterSpec = new IvParameterSpec(iv);

        try {
            mCipher = Cipher.getInstance(AES_TRANSFORMATION);
            mCipher.init(Cipher.DECRYPT_MODE, mSecretKeySpec, mIvParameterSpec);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(mEncryptedFile != null
//                && mEncryptedFile.exists()
//                && mEncryptedFile.length() > 0) {
//            Log.d(getClass().getCanonicalName(),"file deleted");
//            mEncryptedFile.delete();
//        }
    }

    private boolean hasFile() {
        return mEncryptedFile != null
                && mEncryptedFile.exists()
                && mEncryptedFile.length() > 0;
    }

    public void encryptVideo(View view) {
//        if (hasFile()) {
//            Log.d(getClass().getCanonicalName(), "encrypted file found, no need to recreate");
//            return;
//        }
//        try {
//            Cipher encryptionCipher = Cipher.getInstance(AES_TRANSFORMATION);
//            encryptionCipher.init(Cipher.ENCRYPT_MODE, mSecretKeySpec, mIvParameterSpec);
//            // TODO:
//            // you need to encrypt a video somehow with the same key and iv...  you can do that yourself and update
//            // the ciphers, key and iv used in this demo, or to see it from top to bottom,
//            // supply a url to a remote unencrypted file - this method will download and encrypt it
//            // this first argument needs to be that url, not null or empty...
//            new DownloadAndEncryptFileTask("https://sample-videos.com/video123/mp4/240/big_buck_bunny_240p_30mb.mp4", mEncryptedFile, encryptionCipher).execute();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void playVideo(View view) {
        play("1!asertg7*a".toCharArray());
      /*  DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);

//    TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        mSimpleExoPlayerView.setPlayer(player);
        DataSource.Factory dataSourceFactory = new EncryptedFileDataSourceFactory(mCipher, mSecretKeySpec, mIvParameterSpec, bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        try {
            Log.d("Playing location","Data"+mEncryptedFile.getAbsolutePath());
            Uri uri = Uri.fromFile(mEncryptedFile);
            MediaSource videoSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
            player.prepare(videoSource);
            player.setPlayWhenReady(true);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public void play( char[] password)
    {
        try {
            Security.addProvider(new BouncyCastleProvider());
            //Read Salt and IV
            byte[] salt = new byte[SALT_LENGTH];
//        encFile.read(salt);

            byte[] IV = new byte[IV_LENGTH];
//        encFile.read(IV);

            //Get file size
//        long fileSize = new File(filename).length();

            System.out.println("\nGenerating Encryption Key...");

            //Generate the Encryption Key
            byte[] encryptionKey = PBKDF2(password, salt);

            System.out.println("Decrypting Data...\n");

            //AES Cipher Settings
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptionKey, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(IV));
            mIvParameterSpec = new IvParameterSpec(IV);





            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);

//    TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();
            SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            mSimpleExoPlayerView.setPlayer(player);
            DataSource.Factory dataSourceFactory = new EncryptedFileDataSourceFactory(cipher, secretKeySpec, mIvParameterSpec, bandwidthMeter);
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            try {
                Log.d("Playing location","Data"+mEncryptedFile.getAbsolutePath());
                Uri uri = Uri.fromFile(mEncryptedFile);
                MediaSource videoSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
                player.prepare(videoSource);
                player.setPlayWhenReady(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        catch (Exception e)
        {
e.printStackTrace();
        }
    }

    private static byte[] PBKDF2(char[] password, byte[] salt) {
        try {
//            [C@17ec0b5
            Log.d("kko",password.length+"");
//        Use PBKDF2WithHmacSHA512 for java 8
//    SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec spec = new PBEKeySpec(password, salt, PBKDF2_ITERATIONS, KEY_LENGTH);
            SecretKey secretKey = secretKeyFactory.generateSecret(spec);

            return secretKey.getEncoded();
        }
        catch(Exception error)
        {
            System.out.println("Error: " + error.getMessage());
            return null;
        }
    }

    public void Split(View view) {


    }
}
