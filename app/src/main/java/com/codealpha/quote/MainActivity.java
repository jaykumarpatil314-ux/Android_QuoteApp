package com.codealpha.quote;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Native Android Mobile Application in Java
 * CodeAlpha Task 2: Random Quote Generator
 * Connects to CodeAlpha Java REST Backend on port 8080
 */
public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextView tvQuoteText, tvQuoteAuthor, tvQuoteCategory;
    private Button btnNewQuote, btnSpeak, btnCopy, btnShare;
    private TextToSpeech textToSpeech;

    private final List<Quote> localQuotes = new ArrayList<>();
    private Quote currentQuote;

    private static final String BACKEND_URL = "http://10.0.2.2:8080/api/quotes?random=true";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initLocalQuotes();
        textToSpeech = new TextToSpeech(this, this);

        btnNewQuote.setOnClickListener(v -> displayRandomQuote());
        btnSpeak.setOnClickListener(v -> speakQuote());
        btnCopy.setOnClickListener(v -> copyQuoteToClipboard());
        btnShare.setOnClickListener(v -> shareQuote());

        displayRandomQuote();
    }

    private void initViews() {
        tvQuoteText = findViewById(R.id.tv_quote_text);
        tvQuoteAuthor = findViewById(R.id.tv_quote_author);
        tvQuoteCategory = findViewById(R.id.tv_quote_category);
        btnNewQuote = findViewById(R.id.btn_new_quote);
        btnSpeak = findViewById(R.id.btn_speak);
        btnCopy = findViewById(R.id.btn_copy);
        btnShare = findViewById(R.id.btn_share);
    }

    private void initLocalQuotes() {
        localQuotes.add(new Quote("Programs must be written for people to read, and only incidentally for machines to execute.", "Harold Abelson", "Technology"));
        localQuotes.add(new Quote("Simplicity is prerequisite for reliability.", "Edsger W. Dijkstra", "Technology"));
        localQuotes.add(new Quote("The future belongs to those who believe in the beauty of their dreams.", "Eleanor Roosevelt", "Inspiration"));
        localQuotes.add(new Quote("In the middle of every difficulty lies opportunity.", "Albert Einstein", "Inspiration"));
        localQuotes.add(new Quote("We are what we repeatedly do. Excellence, then, is not an act, but a habit.", "Aristotle", "Wisdom"));
        localQuotes.add(new Quote("Success is not final, failure is not fatal: it is the courage to continue that counts.", "Winston Churchill", "Success"));
    }

    private void displayRandomQuote() {
        // Animate quote change
        tvQuoteText.animate().alpha(0f).setDuration(150).withEndAction(() -> {
            // First attempt to fetch from Java Backend in background
            new Thread(() -> {
                try {
                    URL url = new URL(BACKEND_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(1500);

                    if (conn.getResponseCode() == 200) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) sb.append(line);
                        br.close();

                        // Simple JSON extractor
                        String json = sb.toString();
                        String text = extractJsonField(json, "text");
                        String author = extractJsonField(json, "author");
                        String category = extractJsonField(json, "category");

                        runOnUiThread(() -> renderQuote(new Quote(text, author, category)));
                        return;
                    }
                } catch (Exception ignored) {}

                // Fallback to local randomized pool
                runOnUiThread(() -> {
                    int r = new Random().nextInt(localQuotes.size());
                    renderQuote(localQuotes.get(r));
                });
            }).start();
        }).start();
    }

    private void renderQuote(Quote q) {
        currentQuote = q;
        tvQuoteText.setText("“" + q.getText() + "”");
        tvQuoteAuthor.setText("— " + q.getAuthor());
        tvQuoteCategory.setText(q.getCategory().toUpperCase());
        tvQuoteText.animate().alpha(1f).setDuration(250).start();
    }

    private String extractJsonField(String json, String key) {
        try {
            int idx = json.indexOf("\"" + key + "\":\"");
            if (idx != -1) {
                int start = idx + key.length() + 4;
                int end = json.indexOf("\"", start);
                return json.substring(start, end);
            }
        } catch (Exception ignored) {}
        return "";
    }

    private void speakQuote() {
        if (currentQuote != null && textToSpeech != null) {
            String text = currentQuote.getText() + ". By " + currentQuote.getAuthor();
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "quote_tts");
            Toast.makeText(this, "Speaking quote...", Toast.LENGTH_SHORT).show();
        }
    }

    private void copyQuoteToClipboard() {
        if (currentQuote != null) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Quote", "“" + currentQuote.getText() + "” — " + currentQuote.getAuthor());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareQuote() {
        if (currentQuote != null) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "“" + currentQuote.getText() + "” — " + currentQuote.getAuthor() + "\n#CodeAlpha #QuoteOfTheDay");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share Quote via"));
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale.US);
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
