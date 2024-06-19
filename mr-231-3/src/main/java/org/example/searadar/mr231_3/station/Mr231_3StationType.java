package org.example.searadar.mr231_3.station;

import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;

import org.example.searadar.mr231_3.convert.Mr231_3Converter;

import java.nio.charset.Charset;

public class Mr231_3StationType {

    private static final String STATION_TYPE = "МР-231-3";
    private static final String CODEC_NAME = "mr231-3";


    protected void doInitialize() {
        TextLineCodecFactory textLineCodecFactory = new TextLineCodecFactory(
                Charset.defaultCharset(),
                LineDelimiter.UNIX,
                LineDelimiter.CRLF
        );

    }


    public Mr231_3Converter createConverter() {
        return new Mr231_3Converter();
    }
}
