package pro.android.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class ImageFromUrl {
	private Drawable drawable = null;

	public Drawable getDrawable() {
		return drawable;
	}
	public ImageFromUrl(Context ctx, String url) {
		try {
			InputStream is = (InputStream) this.fetch(url);
			drawable = Drawable.createFromStream(is, "src");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}
}
