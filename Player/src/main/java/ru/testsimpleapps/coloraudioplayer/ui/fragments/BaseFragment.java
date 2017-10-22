package ru.testsimpleapps.coloraudioplayer.ui.fragments;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import ru.testsimpleapps.coloraudioplayer.R;


public abstract class BaseFragment extends Fragment {

    public static final String TAG = BaseFragment.class.getSimpleName();
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
            fragmentTransaction.setCustomAnimations(R.anim.fragment_open,
                    R.anim.fragment_close,
                    R.anim.fragment_open,
                    R.anim.fragment_close);

            if (isAdd) {
                fragmentTransaction.add(R.id.frame_container, fragment);
            } else {
                fragmentTransaction.replace(R.id.frame_container, fragment);
            }

            if (tag != null) {
                fragmentTransaction.addToBackStack(tag);
            }

            fragmentTransaction.commit();
        }
    }


}
