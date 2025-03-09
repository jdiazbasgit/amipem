package amipem.generalelectric.generalelectric;

import static android.view.View.INVISIBLE;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("GENERAL ELECTRIC");

        ImageView logo=findViewById(R.id.image);
        logo.setImageResource(R.drawable.ge_vernova_logo_letras_verdes);
        logo.setX(0);
        logo.setY(0);
    }
}