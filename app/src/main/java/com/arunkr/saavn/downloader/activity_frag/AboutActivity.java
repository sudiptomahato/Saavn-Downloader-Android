package com.arunkr.saavn.downloader.activity_frag;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.arunkr.saavn.downloader.R;

/**
 * Created by Arun Kumar Shreevastava on 23/10/16.
 */

public class AboutActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        SpannableString ss = new SpannableString(getString(R.string.click_to_install));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView)
            {
                final String appPackageName = getString(R.string.saavn_package_name); // getPackageName() from Context or Activity object
                try
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                }
                catch (android.content.ActivityNotFoundException anfe)
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView textView = (TextView) findViewById(R.id.install_saavn);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);

        SpannableString ss2 = new SpannableString(getString(R.string.about_me));
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View textView)
            {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/arunKumarNOP")));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        String about_me = getString(R.string.about_me);
        String to_find = "Arun Kumar Shreevastava";

        int index = about_me.indexOf(to_find);

        ss2.setSpan(clickableSpan2, index, index+to_find.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView txtDisclaimer = (TextView) findViewById(R.id.about_me);
        txtDisclaimer.setText(ss2);
        txtDisclaimer.setMovementMethod(LinkMovementMethod.getInstance());
        txtDisclaimer.setHighlightColor(Color.TRANSPARENT);
    }
}
