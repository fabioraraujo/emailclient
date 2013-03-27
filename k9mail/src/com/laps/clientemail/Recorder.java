package com.laps.clientemail;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

public class Recorder {

	/*
	 * Autor: José Filho
	 * @LAPS
	 */

	public Recorder(Context c, Runnable r) {

		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		mFileName += "/teste_msg_record_k-9.3gp";

		this.context = c;
		mTelaAlerta = new AlertDialog.Builder(context);
		mTelaAlerta.setTitle("Gravando a mensagem...");
		mTimer = new TextView(this.context);
		mTimer.setText("Tempo: " + time_hour + ":" + time_min + ":" + time_sec);
		mTimer.setGravity(Gravity.CENTER_HORIZONTAL);
		mTimer.setPadding(10, 10, 10, 10);
		mTimer.setTextSize(20);
		mTelaAlerta.setView(mTimer);
		mTelaAlerta.setCancelable(false);
		
		run = r;
		
		mTelaAlerta.setNeutralButton("Parar", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				stopRecording();
				timeStop();
				onRecordFinish(run);
			}
		});

		mTelaAlerta.show();
		startRecording();
		timeStart();

	}

	private Handler mHandlerTime = new Handler();
	private TextView mTimer;
	private Runnable run;
	private Context context;
	private AlertDialog.Builder mTelaAlerta;
	private boolean isRecording;
	private static int time_sec;
	private int time_min;
	private int time_hour;

	private static final String LOG_TAG = "AudioRecordTest";

	public static String mFileName = null;

	private MediaRecorder mRecorder = null;

	private MediaPlayer mPlayer = null;

	private void startRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}

		mRecorder.start();
	}

	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

	public void onPauseRecorder() {
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}

		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}

	public void timeStart() {
		isRecording = true;
		new Thread() {
			@Override
			public void run() {
				while (isRecording) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					time_sec += 1;
					if (time_sec == 60) {
						time_sec = 0;
						time_min += 1;
					}
					if (time_min == 60) {
						time_hour = +1;
						time_min = 0;
						time_sec = 0;
					}
					mHandlerTime.post(new Runnable() {
						@Override
						public void run() {
							mTimer.setText("Tempo: "
									+ String.format("%02d", time_hour) + ":"
									+ String.format("%02d", time_min) + ":"
									+ String.format("%02d", time_sec));
						}
					});

				}
				super.run();
			}
		}.start();
	}

	public void timeStop() {
		isRecording = false;
	}

	
	
	public void onRecordFinish(Runnable r){
		new Handler().post(r);
		
		Log.i("info","executou o runnable!");
	}

}