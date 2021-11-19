package com.adamk33n3r.tts;


import javax.inject.Inject;
import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;
import java.beans.PropertyVetoException;
import java.util.Locale;

public class TTSSynth {
//    @Inject
//    private NarrationConfig config;

    private Synthesizer synth;

    public TTSSynth() {
        System.setProperty("freetts.voices",
                "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        Voice kevinHQ = new Voice("kevin16", Voice.GENDER_DONT_CARE, Voice.AGE_DONT_CARE, null);
        SynthesizerModeDesc generalDesc = new SynthesizerModeDesc(
            null,      // engine name
            "general",  // mode name
            Locale.US, // locale
            null,      // running
            null);     // voice
        try {
            Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");
            this.synth = Central.createSynthesizer(generalDesc);
            if (this.synth == null) {
                return;
            }

            this.synth.allocate();
            this.synth.getSynthesizerProperties().setVoice(kevinHQ);
            this.synth.resume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        try {
            this.synth.deallocate();
        } catch (EngineException e) {
            e.printStackTrace();
        }
    }

    public boolean say(String message) {
        return this.speak(message);
    }

    public void sayAsync(String message) {
        new Thread(() -> {
            this.speak(message);
        }).start();
    }

    public void pause() {
        this.synth.pause();
    }

    public void resume() {
        try {
            this.synth.resume();
        } catch (AudioException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        this.synth.cancelAll();
    }

    private boolean speak(String message) {
        this.resume();

        // Normalize around 0.5 - 1 because anything below 0.5 you can't even hear anyway
//        float vol = this.config.volume() / 20f + 0.5f;
        float vol = 8 / 20f + 0.5f;
        try {
            this.synth.getSynthesizerProperties().setVolume(vol);
            this.synth.getSynthesizerProperties().setSpeakingRate(150);
            this.synth.getSynthesizerProperties().setPitch(100);
//            this.synth.getSynthesizerProperties().setSpeakingRate(this.config.wpm());
//            this.synth.getSynthesizerProperties().setPitch(this.config.pitch());
        } catch (PropertyVetoException pve) {
            pve.printStackTrace();
        }

//        String sanitizedMessage = this.sanitize(message);
//        this.synth.speakPlainText(sanitizedMessage, null);
        this.synth.speakPlainText(message, null);
        try {
            this.synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    private String sanitize(String message) {
        return message;
//        return Text.sanitizeMultilineText(
//            message
//                // Replace hyphens with spaces. It has trouble processing utterances.
//                .replaceAll("-", " ")
//                // The synthesizer seems to treat an ellipsis as nothing. Replace it with a period.
//                .replaceAll("\\.\\.\\.", ". ")
//        );
    }
}
