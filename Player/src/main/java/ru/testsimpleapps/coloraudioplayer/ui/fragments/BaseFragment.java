package ru.testsimpleapps.coloraudioplayer.ui.fragments;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


public abstract class BaseFragment extends Fragment {

    public static final String NAME_EXTRAS = "NAME_EXTRAS";
    public static final String INTENT_TYPE_MESSAGE = "text/plain";
    public static final String INTENT_TYPE_MAIL = "message/rfc822";

    protected FragmentManager mFragmentManager;
    protected FragmentManager mParentFragmentManager;

    protected void showFragment(@NonNull final Fragment fragment, @Nullable final String tag, boolean isAdd) {
        if (mFragmentManager == null) {
            mFragmentManager = getFragmentManager();
        }
        changeFragment(mFragmentManager, fragment, tag, isAdd);
    }

    protected void showParentFragment(@NonNull final Fragment fragment, @Nullable final String tag) {
        if (mParentFragmentManager == null) {
            if (getParentFragment() != null) {
                mParentFragmentManager = getParentFragment().getFragmentManager();
            }
        }
        changeFragment(mParentFragmentManager, fragment, tag, true);
    }

    private void changeFragment(@NonNull FragmentManager fragmentManager,
                                @NonNull final Fragment fragment,
                                @Nullable final String tag,
                                boolean isAdd) {
        if (fragment != null) {
            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.setCustomAnimations(R.anim.open_fragment,
//                    R.anim.close_fragment,
//                    R.anim.open_fragment,
//                    R.anim.close_fragment);
//
//            if (isAdd) {
//                fragmentTransaction.add(R.id.frame_container, fragment);
//            } else {
//                fragmentTransaction.replace(R.id.frame_container, fragment);
//            }

            if (tag != null) {
                fragmentTransaction.addToBackStack(tag);
            }

            fragmentTransaction.commit();
        }
    }

//    protected void sendIntentText(final String body, @Nullable final String pkg) {
//        if (body != null) {
//            final Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType(INTENT_TYPE_MESSAGE);
//
//            // Set specific package
//            if (pkg != null) {
//                intent.setPackage(pkg);
//            }
//
//            // Set text for send
//            intent.putExtra(android.content.Intent.EXTRA_TEXT, body);
//            startActivity(Intent.createChooser(intent, getString(R.string.detail_share_header)));
//        }
//    }
//
//    protected void sendIntentMail(final String to, final String subject, final String body) {
//        if (body != null) {
//            final Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType(INTENT_TYPE_MAIL);
//            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
//            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
//            intent.putExtra(android.content.Intent.EXTRA_TEXT, body);
//            startActivity(Intent.createChooser(intent, getString(R.string.detail_share_header)));
//        }
//    }

}
