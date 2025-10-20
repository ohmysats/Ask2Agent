/*
 * Copyright (c) 2024 Ask2Agent Contributors
 * All Rights Reserved.
 */

package me.zhanghai.android.textselectionwebsearch;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OllamaResultActivity extends Activity {

    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_CONTENT = "content";
    private static final String EXTRA_PLAIN_TEXT = "plain_text";

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private ImageButton mCloseButton;
    private ImageButton mCopyButton;
    private String mPlainText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remover a barra de título padrão para um visual mais limpo
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_ollama_result);

        // Inicializar views
        mWebView = findViewById(R.id.webview);
        mProgressBar = findViewById(R.id.progress_bar);
        mCloseButton = findViewById(R.id.button_close);
        mCopyButton = findViewById(R.id.button_copy);

        // Recuperar dados do Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra(EXTRA_TITLE);
        String content = intent.getStringExtra(EXTRA_CONTENT);
        mPlainText = intent.getStringExtra(EXTRA_PLAIN_TEXT);

        // Configurar WebView
        configureWebView();

        // Carregar conteúdo HTML
        if (content != null) {
            loadHtmlContent(content);
        }

        // Configurar listeners dos botões
        mCloseButton.setOnClickListener(v -> finish());
        mCopyButton.setOnClickListener(v -> copyToClipboard());
    }

    private void configureWebView() {
        // Configurações de segurança e performance
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setMixedContentMode(
                android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // Configurar WebViewClient para gerenciar o carregamento
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
                mWebView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressBar.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.GONE);
            }
        });
    }

    private void loadHtmlContent(@NonNull String htmlContent) {
        // Envolver o conteúdo em um HTML bem formatado com CSS responsivo
        String fullHtml = buildHtmlPage(htmlContent);
        mWebView.loadDataWithBaseURL(null, fullHtml, "text/html", "UTF-8", null);
    }

    @NonNull
    private String buildHtmlPage(@NonNull String content) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <style>\n" +
                "        * {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            box-sizing: border-box;\n" +
                "        }\n" +
                "        body {\n" +
                "            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;\n" +
                "            line-height: 1.6;\n" +
                "            color: #202124;\n" +
                "            background-color: #ffffff;\n" +
                "            padding: 16px;\n" +
                "        }\n" +
                "        @media (prefers-color-scheme: dark) {\n" +
                "            body {\n" +
                "                background-color: #1f1f1f;\n" +
                "                color: #e8eaed;\n" +
                "            }\n" +
                "        }\n" +
                "        .container {\n" +
                "            max-width: 100%;\n" +
                "            word-wrap: break-word;\n" +
                "        }\n" +
                "        h1, h2, h3, h4, h5, h6 {\n" +
                "            margin-top: 16px;\n" +
                "            margin-bottom: 8px;\n" +
                "            font-weight: 600;\n" +
                "        }\n" +
                "        p {\n" +
                "            margin-bottom: 12px;\n" +
                "        }\n" +
                "        pre {\n" +
                "            background-color: #f5f5f5;\n" +
                "            padding: 12px;\n" +
                "            border-radius: 4px;\n" +
                "            overflow-x: auto;\n" +
                "            margin: 12px 0;\n" +
                "        }\n" +
                "        @media (prefers-color-scheme: dark) {\n" +
                "            pre {\n" +
                "                background-color: #2d2d2d;\n" +
                "            }\n" +
                "        }\n" +
                "        code {\n" +
                "            font-family: 'Courier New', monospace;\n" +
                "            font-size: 0.9em;\n" +
                "        }\n" +
                "        a {\n" +
                "            color: #1a73e8;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "        a:visited {\n" +
                "            color: #681da8;\n" +
                "        }\n" +
                "        blockquote {\n" +
                "            border-left: 4px solid #dadce0;\n" +
                "            padding-left: 12px;\n" +
                "            margin: 12px 0;\n" +
                "            color: #5f6368;\n" +
                "        }\n" +
                "        @media (prefers-color-scheme: dark) {\n" +
                "            blockquote {\n" +
                "                border-left-color: #5f6368;\n" +
                "                color: #9aa0a6;\n" +
                "            }\n" +
                "        }\n" +
                "        table {\n" +
                "            border-collapse: collapse;\n" +
                "            width: 100%;\n" +
                "            margin: 12px 0;\n" +
                "        }\n" +
                "        th, td {\n" +
                "            border: 1px solid #dadce0;\n" +
                "            padding: 8px;\n" +
                "            text-align: left;\n" +
                "        }\n" +
                "        @media (prefers-color-scheme: dark) {\n" +
                "            th, td {\n" +
                "                border-color: #5f6368;\n" +
                "            }\n" +
                "        }\n" +
                "        th {\n" +
                "            background-color: #f8f9fa;\n" +
                "        }\n" +
                "        @media (prefers-color-scheme: dark) {\n" +
                "            th {\n" +
                "                background-color: #2d2d2d;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                content +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }

    private void copyToClipboard() {
        if (mPlainText != null && !mPlainText.isEmpty()) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Ollama Result", mPlainText);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.toast_copied_to_clipboard, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Cria um Intent para iniciar esta Activity com o resultado do Ollama
     */
    public static Intent createIntent(@NonNull Context context, @NonNull String title,
                                      @NonNull String htmlContent, @NonNull String plainText) {
        Intent intent = new Intent(context, OllamaResultActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_CONTENT, htmlContent);
        intent.putExtra(EXTRA_PLAIN_TEXT, plainText);
        return intent;
    }
}