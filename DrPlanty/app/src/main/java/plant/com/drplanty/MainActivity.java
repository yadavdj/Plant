package plant.com.drplanty;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int INPUT_SIZE = 224;

    private static final String TAG = "MainActivity";
    private Classifier classifier;

    private Executor executor = Executors.newSingleThreadExecutor();
    private TextView textViewResult;
    private Button btnDetectObject;
    private ImageView imageViewResult;
    private CameraView cameraView;
    static String plantName;
    String name;

    TextView plantNameTV,causesTV,preventionTV,symptomsTV,diseaseTV;
    DatabaseReference firebase,myReference;
    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraView = findViewById(R.id.cameraView);
        imageViewResult = findViewById(R.id.imageViewResult);
        textViewResult = findViewById(R.id.textViewResult);
        textViewResult.setMovementMethod(new ScrollingMovementMethod());
        btnDetectObject = findViewById(R.id.btnDetectObject);
        LinearLayout linearLayout = findViewById(R.id.bottomlinearLayout);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        plantNameTV = findViewById(R.id.plantNameinfo);
        diseaseTV = findViewById(R.id.diseaseinfo);
        preventionTV = findViewById(R.id.preventioninfo);
        symptomsTV = findViewById(R.id.symptonsinfo);
        causesTV = findViewById(R.id.cuasesinfo);

        firebase = FirebaseDatabase.getInstance().getReference();
        myReference = firebase.child("Plants");


        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

                Bitmap bitmap = cameraKitImage.getBitmap();

                bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

                imageViewResult.setImageBitmap(bitmap);

                final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);

                textViewResult.setText(results.toString());


                myReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        try{

                            if (TensorFlowImageClassifier.getPlantName()!=null){

                                name = dataSnapshot.child(TensorFlowImageClassifier.getPlantName()).child("Plant Name").getValue(String.class);

                                String disease = dataSnapshot.child(TensorFlowImageClassifier.getPlantName()).child("disease").getValue(String.class);

                                String causes = dataSnapshot.child(TensorFlowImageClassifier.getPlantName()).child("causes").getValue(String.class);

                                String symptoms = dataSnapshot.child(TensorFlowImageClassifier.getPlantName()).child("symptoms").getValue(String.class);

                                String preventions = dataSnapshot.child(TensorFlowImageClassifier.getPlantName()).child("preventions").getValue(String.class);
                                Log.d(TAG,"name : "+name);

                                plantNameTV.setText(name);
                                preventionTV.setText(preventions);
                                symptomsTV.setText(symptoms);
                                diseaseTV.setText(disease);
                                causesTV.setText(causes);

                            }else {

                                plantNameTV.setText("DrPlanty");
                                preventionTV.setText("Waiting for help you");
                                symptomsTV.setText("");
                                diseaseTV.setText("");
                                causesTV.setText("");


                            }


                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

        btnDetectObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.captureImage();
            }
        });

        initTensorFlowAndLoadModel();

    }


    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                classifier.close();
            }
        });
    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            startActivity.getModelPath(),
                            startActivity.getLabelPath(),
                            INPUT_SIZE);
                    makeButtonVisible();
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    private void makeButtonVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnDetectObject.setVisibility(View.VISIBLE);
            }
        });
    }
}
