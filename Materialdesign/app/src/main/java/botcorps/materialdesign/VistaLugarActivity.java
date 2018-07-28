package botcorps.materialdesign;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Juan - PC on 18/12/2017.
 */

public class VistaLugarActivity extends AppCompatActivity {
    private long id;
    private Lugar lugar;
    final static int RESULTADO_EDITAR = 1;
    private ImageView imageView;
    final static int RESULTADO_GALERIA= 2;
    final static int RESULTADO_FOTO= 3;
    private Uri uriFoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_lugar);
        Bundle extras = getIntent().getExtras();
        id = extras.getLong("id", -1);
        lugar= MainActivity.lugares.elemento((int) id);
        imageView = (ImageView) findViewById(R.id.foto);
        actualizarVistas(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vista_lugar, menu);
        return true;
    }
    public void galeria(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULTADO_GALERIA);
    }
    public void lanzareditar(View view){
        Intent io = new Intent(this, EdicionLugarActivity.class);
        io.putExtra("id",id-1);
        startActivityForResult(io, RESULTADO_EDITAR);
    }
    public void actualizarVistas(View view){
        TextView nombre = (TextView) findViewById(R.id.nombre);
        nombre.setText(lugar.getNombre());
        ImageView logo_tipo = (ImageView) findViewById(R.id.logo_tipo);
        logo_tipo.setImageResource(lugar.getTipo().getRecurso());
        TextView tipo = (TextView) findViewById(R.id.tipo);
        tipo.setText(lugar.getTipo().getTexto());
        TextView direccion = (TextView) findViewById(R.id.direccion);
        direccion.setText(lugar.getDireccion());
        TextView telefono = (TextView) findViewById(R.id.telefono);
        telefono.setText(Integer.toString(lugar.getTelefono()));
        TextView url = (TextView) findViewById(R.id.url);
        url.setText(lugar.getUrl());
        TextView comentario = (TextView) findViewById(R.id.comentario);
        comentario.setText(lugar.getComentario());
        TextView fecha = (TextView) findViewById(R.id.fecha);
        fecha.setText(DateFormat.getDateInstance().format(
                new Date(lugar.getFecha())));
        TextView hora = (TextView) findViewById(R.id.hora);
        hora.setText(DateFormat.getTimeInstance().format(
                new Date(lugar.getFecha())));
        RatingBar valoracion = (RatingBar) findViewById(R.id.valoracion);
        valoracion.setRating(lugar.getValoracion());
        valoracion.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener() {
                    @Override public void onRatingChanged(RatingBar ratingBar,
                                                          float valor, boolean fromUser) {
                        lugar.setValoracion(valor);
                    }
                });
        ponerFoto(imageView, lugar.getFoto());
    }
    public void verMapa(View view) {
        Uri uri;
        double lat = lugar.getPosicion().getLatitud();
        double lon = lugar.getPosicion().getLongitud();
        if (lat != 0 || lon != 0) {
            uri = Uri.parse("geo:" + lat + "," + lon);
        } else {
            uri = Uri.parse("geo:0,0?q=" + lugar.getDireccion());
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    public void llamadaTelefono(View view) {
        startActivity(new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:" + lugar.getTelefono())));
    }
    public void pgWeb(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(lugar.getUrl())));
    }
    protected void ponerFoto(ImageView imageView, String uri) {
        if (uri != null) {
            imageView.setImageBitmap(reduceBitmap(this, uri, 1024,   1024));
        } else{
            imageView.setImageBitmap(null);
        }
    }
    public static Bitmap reduceBitmap(Context contexto, String uri,
                                      int maxAncho, int maxAlto) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(contexto.getContentResolver()
                    .openInputStream(Uri.parse(uri)), null, options);
            options.inSampleSize = (int) Math.max(
                    Math.ceil(options.outWidth / maxAncho),
                    Math.ceil(options.outHeight / maxAlto));
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(contexto.getContentResolver()
                    .openInputStream(Uri.parse(uri)), null, options);
        } catch (FileNotFoundException e) {
            Toast.makeText(contexto, "Fichero/recurso no encontrado",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        }
    }
    public void tomarFoto(View view) {
        Intent intent = new Intent("MediaStore.ACTION_IMAGE_CAPTURE");
        uriFoto = Uri.fromFile(new File(
                Environment.getExternalStorageDirectory() + File.separator
                        + "img_" + (System.currentTimeMillis() / 1000) + ".jpg"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
        startActivityForResult(intent, RESULTADO_FOTO);
    }
    public void eliminarFoto(View view) {
        lugar.setFoto(null);
        ponerFoto(imageView, null);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accion_compartir:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,
                        lugar.getNombre() + " - " + lugar.getUrl());
                startActivity(intent);
                return true;
            case R.id.accion_llegar:
                verMapa(null);
                return true;
            case R.id.accion_editar:
                lanzareditar(null);
                finish();
                return true;
            case R.id.accion_borrar:
                int _id = MainActivity.adaptador.idPosicion((int) id);
                borrarLugar((int) _id-1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void borrarLugar(final int id) {
        new AlertDialog.Builder(this)
                .setTitle("Borrado de lugar")
                .setMessage("¿Estás seguro que quieres eliminar este lugar?")
                .setPositiveButton("Confirmar",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton) {
                        MainActivity.lugares.borrar(id);
                        MainActivity.adaptador.setCursor(
                                MainActivity.lugares.extraeCursor());
                        MainActivity.adaptador.notifyDataSetChanged();
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        if (requestCode == RESULTADO_EDITAR) {
            actualizarVistas(null);
            findViewById(R.id.scrollView1).invalidate();
        } else if (requestCode == RESULTADO_GALERIA) {
        if (resultCode == Activity.RESULT_OK) {
            lugar.setFoto(data.getDataString());
            ponerFoto(imageView, lugar.getFoto());
        } else {
            Toast.makeText(this, "Foto no cargada", Toast.LENGTH_LONG).show();
        }
        }else if (requestCode == RESULTADO_FOTO) {
            if (resultCode == Activity.RESULT_OK
                    && lugar!=null && uriFoto!=null) {
                lugar.setFoto(uriFoto.toString());
                ponerFoto(imageView, lugar.getFoto());
            } else {
                Toast.makeText(this, "Error en captura", Toast.LENGTH_LONG).show();
            }
        }
    }
}
