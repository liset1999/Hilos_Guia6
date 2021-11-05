package personal.app.hilos_guia6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import personal.app.hilos_guia6.Utils.AudioAsincrono;

public class ReproductorAudioActivity extends AppCompatActivity {

    private Button btnIniciar, btnReiniciar;
    private TextView txvActual, txvFinal;
    private AudioAsincrono audioAsincrono;

    private SeekBar seekbar;
    MediaPlayer reproductorMusica;
    Runnable runnable;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor_audio);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        txvActual = findViewById(R.id.txvActual);
        txvFinal  = findViewById(R.id.txvFinal);
        btnIniciar = findViewById(R.id.btnIniciar);
        btnReiniciar = findViewById(R.id.btnReiniciar);
        //txvPercentage = findViewById(R.id.txvPorcentaje);

        //handler
        handler = new Handler();

        // SeekBar
        seekbar = (SeekBar) findViewById(R.id.seekBar);

        //MediaPlayer
        reproductorMusica = MediaPlayer.create(this, R.raw.nokia_tune);
        reproductorMusica.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
        reproductorMusica.setOnPreparedListener(mp -> {
            seekbar.setMax(reproductorMusica.getDuration());// Valor Final
            playCycle();
            //reproductorMusica.start();
        });
//
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            //hace un llamado a la perilla cuando se arrastra
            @Override
            public void onProgressChanged(SeekBar seekBar,
                                          int i/*progress*/,
                                          boolean b/*fromUser*/) {
                if(b){
                    progressChangedValue = i;
                    //Log.i("Tranverse Change: ", Integer.toString(i) );
                    reproductorMusica.seekTo(i);
                    seekbar.setProgress(i);
                    //mostrarPorcentaje.setText(String.valueOf(/*progress*/i)+" %");
                }
            }

            //hace un llamado  cuando se toca la perilla
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            //hace un llamado  cuando se detiene la perilla
            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(ReproductorAudioActivity.this,
                        "Seek bar progress is :" + progressChangedValue,
                        Toast.LENGTH_SHORT).show();
            }
        });
        

        btnIniciar.setOnClickListener(v -> {
            iniciar();
        });

        btnReiniciar.setOnClickListener(  v -> {
            reiniciar();
        });
    }

    public void playCycle(){
        // Valor Inicial
        seekbar.setProgress(reproductorMusica.getCurrentPosition() );

        if(reproductorMusica.isPlaying()){
            runnable = () -> playCycle();
            handler.postDelayed(runnable,1000);
        }
    }

    private void iniciar() {

        if ( audioAsincrono == null ) {

            btnIniciar.setText("Pausar");

            audioAsincrono = new AudioAsincrono(
                    ReproductorAudioActivity.this,
                    txvActual, txvFinal
            );
            //super.onResume();
            reproductorMusica.start();
            playCycle();
            audioAsincrono.execute();

            Toast.makeText(
                    ReproductorAudioActivity.this,
                    "Reproduciendo",
                    Toast.LENGTH_SHORT).show();

        } else
        if ( audioAsincrono.getStatus() == AsyncTask.Status.FINISHED ) {

            audioAsincrono = new AudioAsincrono(ReproductorAudioActivity.this,
                    txvActual, txvFinal);
            //super.onResume();
            reproductorMusica.start();
            playCycle();
            audioAsincrono.execute();

            Toast.makeText(
                    ReproductorAudioActivity.this,
                    "Reproduciendo", Toast.LENGTH_SHORT).show();

        } else
        if ( audioAsincrono.getStatus() == AsyncTask.Status.RUNNING && !audioAsincrono.esPause() )
        {
            // En caso de que este corriendo y no este pausado; entonces se pausa. pause=true.

            btnIniciar.setText("Reanudar");

            //super.onPause();
            reproductorMusica.pause();
            audioAsincrono.pausarAudio();

        } else
        if ( audioAsincrono.esPause() )
        {
            // En caso de que este pausado; entonces se debe reanudar

            btnIniciar.setText("Pausar");

            //super.onResume();
            reproductorMusica.start();  //
            playCycle();
            audioAsincrono.reanudarAudio();
        }
    }

    private void reiniciar(){
        if (audioAsincrono.getStatus() == AsyncTask.Status.RUNNING || audioAsincrono.esPause() )
        {
            // En caso de que este corriendo o este pausado; entonces se reinicia.
            audioAsincrono.reiniciarAudio();

            btnIniciar.setText("Pausar");

            audioAsincrono = new AudioAsincrono(
                    ReproductorAudioActivity.this,
                    txvActual, txvFinal
            );

            //super.onResume();
            reproductorMusica.reset();  //
            reproductorMusica.start();  //
            audioAsincrono.execute();
            playCycle();
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

}