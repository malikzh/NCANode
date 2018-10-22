package kz.ncanode.log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class LogPrintStream extends PrintStream {
    public boolean needPrintTime = true;

    public LogPrintStream(OutputStream out) {
        super(out);
    }

    public LogPrintStream(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public LogPrintStream(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
        super(out, autoFlush, encoding);
    }

    public LogPrintStream(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public LogPrintStream(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(fileName, csn);
    }

    public LogPrintStream(File file) throws FileNotFoundException {
        super(file);
    }

    public LogPrintStream(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
    }

    @Override
    public void print(int i) {
        printTime();
        super.print(i);
    }

    @Override
    public void print(boolean b) {
        printTime();
        super.print(b);
    }

    @Override
    public void print(char c) {
        printTime();
        super.print(c);
    }

    @Override
    public void print(long l) {
        printTime();
        super.print(l);
    }

    @Override
    public void print(float f) {
        printTime();
        super.print(f);
    }

    @Override
    public void print(char[] s) {
        printTime();
        super.print(s);
    }

    @Override
    public void print(double d) {
        printTime();
        super.print(d);
    }

    @Override
    public void print(String s) {
        printTime();
        super.print(s);
    }

    @Override
    public void print(Object obj) {
        printTime();
        super.print(obj);
    }

    private void printTime() {

        if (!needPrintTime) return;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());

        String logItem = "[" + dateFormat.format(cal.getTime()) + "] ";

        super.print(logItem);
    }
}
