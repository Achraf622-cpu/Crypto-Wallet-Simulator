package com.crypto.app.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsoleTable {
    private final List<String> headers = new ArrayList<String>();
    private final List<List<String>> rows = new ArrayList<List<String>>();

    public ConsoleTable(String... headers) {
        this.headers.addAll(Arrays.asList(headers));
    }

    public void addRow(String... cols) {
        rows.add(Arrays.asList(cols));
    }

    public String render() {
        int cols = headers.size();
        int[] widths = new int[cols];
        for (int i = 0; i < cols; i++) {
            widths[i] = headers.get(i).length();
        }
        for (List<String> row : rows) {
            for (int i = 0; i < cols; i++) {
                if (i < row.size()) widths[i] = Math.max(widths[i], row.get(i).length());
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(line(widths));
        sb.append(row(headers, widths));
        sb.append(line(widths));
        for (List<String> r : rows) {
            sb.append(row(r, widths));
        }
        sb.append(line(widths));
        return sb.toString();
    }

    private String line(int[] widths) {
        StringBuilder sb = new StringBuilder();
        sb.append('+');
        for (int w : widths) {
            for (int i = 0; i < w + 2; i++) sb.append('-');
            sb.append('+');
        }
        sb.append('\n');
        return sb.toString();
    }

    private String row(List<String> cols, int[] widths) {
        StringBuilder sb = new StringBuilder();
        sb.append('|');
        for (int i = 0; i < widths.length; i++) {
            String v = i < cols.size() ? cols.get(i) : "";
            sb.append(' ').append(padRight(v, widths[i])).append(' ').append('|');
        }
        sb.append('\n');
        return sb.toString();
    }

    private String padRight(String s, int width) {
        if (s.length() >= width) return s;
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < width) sb.append(' ');
        return sb.toString();
    }
}







