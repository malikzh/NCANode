package kz.ncanode.log;

import java.io.*;

public class Log {
    protected OutputStream os = null;
    protected LogPrintStream  ps = null;

    public Log() {
        os = System.out;
        ps = new LogPrintStream(os);
    }

    public Log(OutputStream os) {
        this.os = os;
        ps = new LogPrintStream(os);
    }

    public Log(String file) throws FileNotFoundException {
        os = new FileOutputStream(file, true);
        ps = new LogPrintStream(os);
    }

    public void write(String msg) {
        if (ps == null) return;
        ps.println(msg);
    }

    public LogPrintStream getPrintStream() {
        return ps;
    }

    public OutputStream getOutputStream() {
        return os;
    }
}
