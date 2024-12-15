package demo.config.output;

import framework.configurables.conversions.OutputConverter;
import framework.request.response.ContentType;

public class ColoredOutputConverter implements OutputConverter {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    @Override
    public String stringify(Object o) {
        if(o == null) return "";
        return ANSI_YELLOW + o + ANSI_RESET;
    }

    @Override
    public ContentType getContentType() {
        return null;
    }
}
