package main;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import be.tarsos.dsp.*;
import be.tarsos.dsp.WaveformSimilarityBasedOverlapAdd.Parameters;
import be.tarsos.dsp.effects.DelayEffect;
import be.tarsos.dsp.effects.FlangerEffect;
import be.tarsos.dsp.io.PipedAudioStream;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.TarsosDSPAudioFormat.Encoding;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import be.tarsos.dsp.io.UniversalAudioInputStream;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.io.jvm.WaveformWriter;
import be.tarsos.dsp.pitch.*;
import be.tarsos.dsp.resample.RateTransposer;
import be.tarsos.dsp.resample.Resampler;
import be.tarsos.dsp.util.fft.FFT;
public class Sound {
	static AudioDispatcher dispatcher;
	static AudioPlayer ap;
	public Sound() {
	}
	static void startCli(String source,double cents,double volume,double tempo) throws UnsupportedAudioFileException, IOException{
		File inputFile = new File(source);
		double factor = cents;
		RateTransposer rateTransposer = new RateTransposer(factor);
		FadeOut fi = new FadeOut(0);
		Player pl = new Player(fi,rateTransposer,factor);
		pl.load(inputFile);
		pl.setTempo(tempo);
		pl.setGain(volume);
		if (pl.getDurationInSeconds()<3) {
			pl.play();
		}
		else
			Graphics.setInfo("Длительность файла > 3 сек.!: "+Math.round(pl.getDurationInSeconds()));
	}
	static void startPad(int pad) throws LineUnavailableException, UnsupportedAudioFileException, IOException {
		File file = new File("sounds/pads/"+pad+".wav");
		Clip clip = AudioSystem.getClip();
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
        clip.open(inputStream);
        clip.start(); 
	}
	static void stopCli() {
		dispatcher.stop();
	}
	public static double centToFactor(double cents){
		return 1 / Math.pow(Math.E,cents*Math.log(2)/1200/Math.log(Math.E)); 
	}
}