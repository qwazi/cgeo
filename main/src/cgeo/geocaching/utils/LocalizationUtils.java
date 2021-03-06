package cgeo.geocaching.utils;

import cgeo.geocaching.CgeoApplication;
import cgeo.geocaching.storage.ContentStorage;
import cgeo.geocaching.storage.Folder;
import cgeo.geocaching.storage.PersistableFolder;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * A set of static helper methods supporting localization/internationalization
 * especially in areas where Code has no activity available to get a context from.
 *
 * All methods work also in unit-test environments (where there is no available context
 */
public final class LocalizationUtils {

    private static final Context APPLICATION_CONTEXT =
        CgeoApplication.getInstance() == null ? null : CgeoApplication.getInstance().getApplicationContext();

    private LocalizationUtils() {
        //Util class, no instance
    }

    public static boolean hasContext() {
        return APPLICATION_CONTEXT != null;
    }

    public static String getString(@StringRes final int resId, final Object ... params) {
        return getStringWithFallback(resId, null, params);
    }

    public static String getStringWithFallback(@StringRes final int resId, final String fallback, final Object ... params) {
        if (APPLICATION_CONTEXT == null) {
            return "(NoCtx)" + (fallback == null ? "" : fallback) + "[" + StringUtils.join(params, ";") + "]";
        }
        return APPLICATION_CONTEXT.getString(resId, params);
    }

    public static String getPlural(@PluralsRes final int pluralId, final int quantity) {
        return getPlural(pluralId, quantity, "thing(s)");
    }

    public static String getPlural(@PluralsRes final int pluralId, final int quantity, final String fallback) {
        if (APPLICATION_CONTEXT == null) {
            return quantity + " " + fallback;
        }
        return CgeoApplication.getInstance().getApplicationContext().getResources().getQuantityString(pluralId, quantity, quantity);
    }

    /**
     * Given a resource id and parameters to fill it, constructs one message fit for user display (left) and one for log file
     * (right). Difference is that the one for the log file will contain more detailled information than that for the end user
     */
    public static ImmutablePair<String, String> getMultiPurposeString(@StringRes final int messageId, final String fallback, final Object ... params) {

        //prepare params message
        final Object[] paramsForLog = new Object[params.length];
        final Object[] paramsForUser = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof Folder) {
                paramsForUser[i] = ((Folder) params[i]).toUserDisplayableString();
                paramsForLog[i] = params[i] + "(" + ContentStorage.get().getUriForFolder((Folder) params[i]) + ")";
            } else if (params[i] instanceof PersistableFolder) {
                paramsForUser[i] = ((PersistableFolder) params[i]).toUserDisplayableValue();
                paramsForLog[i] = params[i] + "(" + ContentStorage.get().getUriForFolder(((PersistableFolder) params[i]).getFolder()) + ")";
            } else if (params[i] instanceof Uri) {
                paramsForUser[i] = UriUtils.toUserDisplayableString((Uri) params[i]);
                paramsForLog[i] = params[i];
            } else {
                paramsForUser[i] = params[i];
                paramsForLog[i] = params[i];
            }
        }
        return new ImmutablePair<>(getStringWithFallback(messageId, fallback, paramsForUser), getStringWithFallback(messageId, fallback, paramsForLog));
    }



}
