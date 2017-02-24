package org.kymjs.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Browser;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Various link jump styles
 *
 * @author kymjs (http://www.kymjs.com/) on 8/10/15.
 */
public class UrlUtils {


    /**
     * 让TextViewAutomatically parse URL and highlight settings Click link (link does not support Chinese)
     * Note: deep experience, write a regular is not easy, ah, Android actually does not support POSIX characters
     * <p/>
     * Created by kymjs(www.kymjs.com) on 8/5/15.
     *
     * @param tv      TextView
     * @param content To highlight content
     * @return Has been parsed after the TextView
     */
    public static TextView handleText(TextView tv, String content) {
        SpannableStringBuilder sp = new SpannableStringBuilder(content);
        //But also hit a pit, in the Android \ p {Alnum} and Java \ p {Alnum} is not the same value, I have to change [a-zA-Z0-9]
        Pattern pattern = Pattern.compile("(http|https|ftp|svn)://([a-zA-Z0-9]+[/?.?])" +
                "+[a-zA-Z0-9]*\\??([a-zA-Z0-9]*=[a-zA-Z0-9]*&?)*");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String url = matcher.group();
            int start = content.indexOf(url);
            if (start >= 0) {
                int end = start + url.length();
                sp.setSpan(new URLSpan(url), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                sp.setSpan(getClickableSpan(url), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        tv.setText(sp);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        return tv;
    }

    /**
     * Handle html data highlighting and response
     *
     * @param tv
     * @param content
     * @return
     */
    public static TextView handleHtmlText(TextView tv, String content) {
        SpannableStringBuilder sp = new SpannableStringBuilder(Html.fromHtml(content));
        URLSpan[] urlSpans = sp.getSpans(0, sp.length(), URLSpan.class);
        for (final URLSpan span : urlSpans) {
            int start = sp.getSpanStart(span);
            int end = sp.getSpanEnd(span);
            sp.setSpan(getClickableSpan(span.getURL()), start, end, Spanned
                    .SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tv.setText(sp);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        return tv;
    }

    /**
     * Set link jumps and highlight styles
     *
     * @param url
     * @return
     */
    private static ClickableSpan getClickableSpan(final String url) {
        return new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Uri uri = Uri.parse(url);
                Context context = widget.getContext();
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
                context.startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(false);
            }
        };
    }

}
