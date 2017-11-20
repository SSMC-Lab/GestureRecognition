package com.example.monster.airgesture.phase;


import android.media.AudioFormat;

/**
 * wav文件的包装类
 */
final public class WavHeader {
    public static final short WAV_FORMAT_PCM=1;

    private byte[] header;

    public WavHeader() {
        header = new byte[44];

        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';

        /*
        header[4~7]  存放 整个wav文件的大小（字节数）-8
         */

        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';

        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;

        /*
        header[20~21`] 存放 声音数据格式类别
        例如，header[20]=1;header[21]=0;表示声音的格式类别为线性pcm
         */

        /*
        header[22~23] 存放通道数量
        例如，
        header[22]=1 ;header[23]=0;for molo
        header[22]=2;header[23]=0; for stereo
         */

        /*
        header[24~27]  存放 发声设备的实际发声频率（采样频率，例如，44100Hz）
         */

        /*
        header[28~31] 存放 每秒数据量：通道数*采样频率*每个样本数据位数/8
         */

        /*
        header[32，33] 存放 数据块的调整数（按字节算的）：通道数*每个样本的数据位值/8。播放软件需要一次处理多个该值大小的字节数据，以便将其值用于缓冲区的调整。
         */

        /*
        header[34，35]  存放每个样本的数据位数
         */

        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';

        /*
        header[40~43] 存放 语音数据的大小（字节数量）
         */
    }

    public boolean setAdjustFileLength(long length) {
        header[4] = (byte) (length & 0xff);
        header[5] = (byte) ((length >> 8) & 0xff);
        header[6] = (byte) ((length >> 16) & 0xff);
        header[7] = (byte) ((length >> 24) & 0xff);
        return true;
    }

    public boolean setWaveFormatPcm(short format) {
        header[20] = (byte) (format & 0xff);
        header[21] = (byte) ((format >> 8) & 0xff);
        return true;
    }

    public boolean setChannelCount(int channelIn) {
        short channelCount;
        if (channelIn == AudioFormat.CHANNEL_IN_MONO) {
            channelCount = 1;
        } else if (channelIn == AudioFormat.CHANNEL_IN_STEREO) {
            channelCount = 2;
        } else {
            return false;
        }

        header[22] = (byte) (channelCount & 0xff);
        header[23] = (byte) ((channelCount >> 8) & 0xff);
        return true;
    }


    public boolean setSampleRate(int sampleRate){
        header[24]=(byte)(sampleRate&0xff);
        header[25]=(byte)((sampleRate>>8)&0xff);
        header[26]=(byte)((sampleRate>>16)&0xff);
        header[27]=(byte)((sampleRate>>24)&0xff);
        return true;
    }

    public boolean setByteRate(int channelIn, int sampleRate, int encodingPcm) {
        short channelCount;
        short encodingBit;

        if (channelIn == AudioFormat.CHANNEL_IN_MONO) {
            channelCount = 1;
        } else if (channelIn == AudioFormat.CHANNEL_IN_STEREO) {
            channelCount = 2;
        } else {
            return false;
        }
        if (encodingPcm == AudioFormat.ENCODING_PCM_8BIT) {
            encodingBit = 8;
        } else if (encodingPcm == AudioFormat.ENCODING_PCM_16BIT) {
            encodingBit = 16;
        } else {
            return false;
        }

        int byteRate = channelCount * sampleRate * encodingBit / 8;
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        return true;
    }

    public boolean setBlockAlign(int channelIn, int encodingPcm) {
        short channelCount;
        short encodingBit;
        if (channelIn == AudioFormat.CHANNEL_IN_MONO) {
            channelCount = 1;
        } else if (channelIn == AudioFormat.CHANNEL_IN_STEREO) {
            channelCount = 2;
        } else {
            return false;
        }
        if (encodingPcm == AudioFormat.ENCODING_PCM_8BIT) {
            encodingBit = 8;
        } else if (encodingPcm == AudioFormat.ENCODING_PCM_16BIT) {
            encodingBit = 16;
        } else {
            return false;
        }
        short blockAlign = (short) (channelCount * encodingBit / 8);
        header[32] = (byte) (blockAlign & 0xff);
        header[33] = (byte) ((blockAlign >> 8) & 0xff);
        return true;
    }

    public boolean setEncodingBit(int encodingPcm) {
        short encodingBit;
        if (encodingPcm == AudioFormat.ENCODING_PCM_8BIT) {
            encodingBit = 8;
        } else if (encodingPcm == AudioFormat.ENCODING_PCM_16BIT) {
            encodingBit = 16;
        } else {
            return false;
        }

        header[34] = (byte) (encodingBit & 0xff);
        header[35] = (byte) ((encodingBit >> 8) & 0xff);
        return true;
    }

    public boolean setAudioDataLength(long length) {
        header[40] = (byte) (length & 0xff);
        header[41] = (byte) ((length >> 8) & 0xff);
        header[42] = (byte) ((length >> 16) & 0xff);
        header[43] = (byte) ((length >> 24) & 0xff);
        return true;
    }

    public byte[] getHeader() {
        return header;
    }
}
