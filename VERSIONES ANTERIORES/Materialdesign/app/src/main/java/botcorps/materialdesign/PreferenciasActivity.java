package botcorps.materialdesign;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Juan - PC on 17/12/2017.
 */

public class PreferenciasActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferenciasFragment())
                .commit();
    }

}