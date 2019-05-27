package fishfinder.itkmitl.fishfinder.dialog;

import android.app.AlertDialog;
import android.content.Context;

import dmax.dialog.SpotsDialog;
import fishfinder.itkmitl.fishfinder.R;

public class CustomLoading {

    private AlertDialog loadingDialog;
    public CustomLoading(Context context){
        loadingDialog = new SpotsDialog.Builder().setContext(context).setTheme(R.style.CustomLoadingDialog).build();
    }
    public void showDialog(){
        loadingDialog.show();
    }
    public void dismissDialog(){
        loadingDialog.dismiss();
    }
}
