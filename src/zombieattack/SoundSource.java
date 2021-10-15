package zombieattack;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;

public class SoundSource extends Positionable {
    static List<SoundSource> list = new ArrayList<>();

    static void updateListenerLocation(Vector3f position, Quaternionf rotation) {
        if(!list.isEmpty()) {
            Vector3f at = new Vector3f(1, 1, 1).normalize();
            Vector3f up = new Vector3f(0, 1, 0).normalize().rotateX((float) Math.toRadians(90));

            rotation.transform(at);
            rotation.transform(up);

            float[] orientation = new float[6];
            orientation[0] = at.x;
            orientation[1] = at.y;
            orientation[2] = at.z;
            orientation[3] = up.x;
            orientation[4] = up.y;
            orientation[5] = up.z;

            AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z);
            AL10.alListenerfv(AL10.AL_ORIENTATION, orientation);
        }
    }

    static void init() throws Exception {
        deviceId = ALC10.alcOpenDevice((ByteBuffer)null);

        ALCCapabilities deviceCaps = ALC.createCapabilities(deviceId);
        IntBuffer contextAttribList = BufferUtils.createIntBuffer(16);

        contextAttribList.put(ALC_REFRESH);
        contextAttribList.put(60);

        contextAttribList.put(ALC_SYNC);
        contextAttribList.put(ALC_FALSE);

        contextAttribList.put(ALC_MAX_AUXILIARY_SENDS);
        contextAttribList.put(2);

        contextAttribList.put(0);
        contextAttribList.flip();

        long newContext = ALC10.alcCreateContext(deviceId, contextAttribList);
        if(!ALC10.alcMakeContextCurrent(newContext)) throw new Exception("Failed to make context current");

        AL.createCapabilities(deviceCaps);

        AL10.alListener3f(AL10.AL_ORIENTATION, 0f, 0f, 1f);
        AL10.alListener3f(AL10.AL_POSITION, 0f, 0f, 0f);
    }

    int sourceId;
    long length;
    static long deviceId;
    Thread thread = null;
    float volume = 1;
    boolean playing = false;

    SoundSource(String fileName) throws Exception {
        int bufferId = AL10.alGenBuffers();

        createBufferData(bufferId, fileName);

        sourceId = AL10.alGenSources();
        AL10.alSourcei(sourceId, AL10.AL_BUFFER, bufferId);

        AL10.alSourcef(sourceId, AL10.AL_PITCH, 1);
        AL10.alSourcef(sourceId, AL10.AL_GAIN, volume);

        list.add(this);
    }

    private void createBufferData(int bufferId, String fileName) throws UnsupportedAudioFileException, IOException {
        final int MONO = 1, STEREO = 2;

        AudioInputStream stream = AudioSystem.getAudioInputStream(new File(fileName));

        AudioFormat format = stream.getFormat();
        if(format.isBigEndian()) throw new UnsupportedAudioFileException("Big endian formats unsupported.");

        int openALFormat = -1;
        switch(format.getChannels()) {
            case MONO:
                switch(format.getSampleSizeInBits()) {
                    case 8:
                        openALFormat = AL10.AL_FORMAT_MONO8;
                        break;
                    case 16:
                        openALFormat = AL10.AL_FORMAT_MONO16;
                        break;
                }
                break;
            case STEREO:
                switch(format.getSampleSizeInBits()) {
                    case 8:
                        openALFormat = AL10.AL_FORMAT_STEREO8;
                        break;
                    case 16:
                        openALFormat = AL10.AL_FORMAT_STEREO16;
                        break;
                }
                break;
        }

        byte[] b = new byte[stream.available()];
        stream.read(b);

        ByteBuffer data = BufferUtils.createByteBuffer(b.length).put(b);
        data.flip();

        AL10.alBufferData(bufferId, openALFormat, data, (int)format.getSampleRate());
        length = (long)(1000f * stream.getFrameLength() / format.getFrameRate());
    }

    void setVolume(float volume) {
        this.volume = volume;
        AL10.alSourcef(sourceId, AL10.AL_GAIN, volume);
    }
    float getVolume() {
        return volume;
    }

    @Override
    void setPosition(Vector3f position) {
        super.setPosition(position);
        AL10.alSource3f(sourceId, AL10.AL_POSITION, getPosition().x, getPosition().y, getPosition().z);
    }

    void startPlaying() { // Start playing (looping enabled)
        if (playing) AL10.alSourceStop(sourceId);
        AL10.alSourcei(sourceId, AL10.AL_LOOPING, AL10.AL_TRUE);
        AL10.alSourcePlay(sourceId);
        playing = true;
    }

    void startPlayingAfter(long millis) {
        if (thread != null) thread.interrupt();
        thread = new Thread(() -> {
            try {
                Thread.sleep(millis);
                startPlaying();
            } catch (InterruptedException e) {
                return;
            }
        });
        thread.start();
    }

    void stopPlaying() { // Stop playing
        if (playing) AL10.alSourceStop(sourceId);
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        playing = false;
    }

    void play() { // Play full length
        play(length);
    }

    void play(long millis) { // Play for defined length in milliseconds
        stopPlaying();
        AL10.alSourcei(sourceId, AL10.AL_LOOPING, AL10.AL_FALSE);
        if (thread != null) thread.interrupt();
        playing = true;
        thread = new Thread(() -> {
            AL10.alSourcePlay(sourceId);
            try {
                Thread.sleep(millis); //Wait for the sound to finish
            } catch(InterruptedException ex) {}
            stopPlaying(); //Demand that the sound stop
        });
        thread.start();
    }

    void clean() {
        stopPlaying();
        AL10.alDeleteSources(sourceId);
    }
}