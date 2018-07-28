package botcorps.materialdesign;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Juan - PC on 17/12/2017.
 */
public class PreferenciasFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }
}
