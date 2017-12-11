package ru.testsimpleapps.coloraudioplayer.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ru.testsimpleapps.coloraudioplayer.R;


public abstract class BaseFragment extends Fragment {

    public static final String TAG = BaseFragment.class.getSimpleName();

    protected FragmentManager mFragmentManager;
    protected FragmentManager mParentFragmentManager;
    protected Snackbar mSnackBar;
    protected Toast mToast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        init(container, savedInstanceState);
        return view;
    }

    private void init(final ViewGroup container, final Bundle savedInstanceState) {
        setSnackBar(container);
        setToast();
    }

    private void setSnackBar(final ViewGroup container) {
        mSnackBar = Snackbar.make(container, "", Snackbar.LENGTH_SHORT);
        mSnackBar.setDuration(Snackbar.LENGTH_SHORT);
    }

    private void setToast() {
        mToast = Toast.makeText(getContext(), "", Toast.LENGTH_LONG);
        mToast.setDuration(Toast.LENGTH_SHORT);
    }

    protected void showSnackBar(final int resource) {
        mSnackBar.setText(getResources().getString(resource));
        mSnackBar.show();
    }

    protected void showSnackBar(final String string) {
        mSnackBar.setText(string);
        mSnackBar.show();
    }

    protected void showToast(final int resource) {
        mToast.setText(getResources().getString(resource));
        mToast.show();
    }

    protected void showToast(final String string) {
        mToast.setText(string);
        mToast.show();
    }

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

    public boolean onBackPressed() {
        return false;
    }

    public int getDisplayWidth() {
        final DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public int getDisplayHeight() {
        final DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }


}
