package com.laps.clientemail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

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
	 * 
	 * @LAPS
	 */
	private Handler mHandlerTime = new Handler();
	private TextView mTimer;
	private Runnable run;
	private Context context;
	private AlertDialog.Builder mTelaAlerta;
	private boolean isRecording;
	private static int time_sec;
	private int time_min = 0;
	private int time_hour = 0;

	private static final String LOG_TAG = "AudioRecordTest";
	Date data = new Date(System.currentTimeMillis());
	int day = data.getDay();
	int month = data.getMonth();
	int year = data.getYear();
	int hours = data.getHours();
	int minutes = data.getMinutes();


	public String mFileName = "record_email_client_" + day + month + year + "_"
			+ hours + minutes;
	public String mFileExtention = ".3gp";
	public String mFileDiretory = Environment.getExternalStorageDirectory()
			.getAbsolutePath();
	public String mFileNameBarSeparation = "/";
	public int mFileNameCount = 1;
	// format diretorio/nome_count.formato
	public String mFileCompleteName = mFileDiretory + mFileNameBarSeparation
			+ mFileName + mFileNameCount + mFileExtention;

	private MediaRecorder mRecorder = null;

	private MediaPlayer mPlayer = null;

	public Recorder(Context c, Runnable r) {
		this.context = c;
		this.run = r;
	}

	public void refreshFileCompleteNameByCount() {
		refreshDateFileName();
		mFileNameCount++;
		mFileCompleteName = mFileDiretory + mFileNameBarSeparation + mFileName
				+ mFileNameCount + mFileExtention;
	}

	public void recorderStart() {
		mTelaAlerta = new AlertDialog.Builder(context);
		mTelaAlerta.setTitle("Gravando a mensagem...");
		mTimer = new TextView(this.context);
		mTimer.setText("Tempo: " + String.format("%02d", time_hour) + ":"
				+ String.format("%02d", time_min) + ":"
				+ String.format("%02d", time_sec));
		mTimer.setGravity(Gravity.CENTER_HORIZONTAL);
		mTimer.setPadding(10, 10, 10, 10);
		mTimer.setTextSize(20);
		mTelaAlerta.setView(mTimer);
		mTelaAlerta.setCancelable(false);
		time_hour = time_sec = time_min= 0;

		mTelaAlerta.setPositiveButton("Parar", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				recorderStop();
			}
		});
		mTelaAlerta.show();
		startRecording();
		timeStart();
	}

	public void recorderStop() {
		stopRecording();
		timeStop();
		onRecordFinish(run);
	}

	private void startRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		Log.i(this.LOG_TAG, mFileCompleteName);
		mRecorder.setOutputFile(mFileCompleteName);
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

	private void pauseRecorder() {
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}

		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}

	private void timeStart() {
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

	private void timeStop() {
		isRecording = false;

	}

	public void onRecordFinish(Runnable r) {
		new Handler().post(r);
		Log.i(LOG_TAG, "executou o runnable!");

	}

	public void setRun(Runnable newRun) {
		this.run = newRun;
	}

	private void refreshDateFileName() {
		day = data.getDay();
		month = data.getMonth();
		year = data.getYear();
		hours = data.getHours();
		minutes = data.getMinutes();

		mFileName = "record_email_client_" + day + month + year + "_" + hours
				+ minutes;
	}
}