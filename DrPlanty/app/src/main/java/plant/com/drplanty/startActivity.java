package plant.com.drplanty;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class startActivity extends AppCompatActivity {

    TextView tomato,banana;


    public static String getModelPath() {
        return MODEL_PATH;
    }

    public static String getLabelPath() {
        return LABEL_PATH;
    }

    public static String MODEL_PATH ="tomato_graph.lite";

    public static String LABEL_PATH = "tomato_labels.txt";

    public static void setModelPath(String modelPath) {
        MODEL_PATH = modelPath;
    }

    public static void setLabelPath(String labelPath) {
        LABEL_PATH = labelPath;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        tomato = findViewById(R.id.tomato);
        banana = findViewById(R.id.banana);

        tomato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MODEL_PATH = "tomato_graph.lite";
                LABEL_PATH = "tomato_labels.txt";

                setLabelPath(LABEL_PATH);
                setModelPath(MODEL_PATH);
                Intent intent = new Intent(startActivity.this,MainActivity.class);
                startActivity(intent);


            }
        });

        banana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MODEL_PATH = "optimized_graph.lite";
                LABEL_PATH = "retrained_labels.txt";

                setLabelPath(LABEL_PATH);
                setModelPath(MODEL_PATH);
                Intent intent = new Intent(startActivity.this,MainActivity.class);
                startActivity(intent);


            }
        });



    }
}
