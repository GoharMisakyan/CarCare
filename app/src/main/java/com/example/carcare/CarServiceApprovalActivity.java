package com.example.carcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import com.squareup.picasso.Picasso;

public class CarServiceApprovalActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<CarService> carServiceList;
    private CarServiceAdapter adapter;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_service_approval);

        recyclerView = findViewById(R.id.recycler_view_approval);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        carServiceList = new ArrayList<>();
        adapter = new CarServiceAdapter();
        recyclerView.setAdapter(adapter);

        fStore = FirebaseFirestore.getInstance();

        retrievePendingApprovalRequests();
    }

    private void retrievePendingApprovalRequests() {
        fStore.collection("registrationRequests") // Changed to registrationRequests
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        CarService carService = document.toObject(CarService.class);
                        carServiceList.add(carService);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(CarServiceApprovalActivity.this, "Error retrieving data: " + e.getMessage(),  Toast.LENGTH_SHORT).show());
    }

    private  class CarServiceAdapter extends RecyclerView.Adapter<CarServiceAdapter.CarServiceViewHolder> {
      /*  private List<CarService> carServiceList;
        private FirebaseFirestore fStore;
        private CarServiceApprovalActivity activity;


        public CarServiceAdapter(List<CarService> carServiceList, CarServiceApprovalActivity activity) {
            this.carServiceList = carServiceList;
            this.fStore = fStore;
        }*/

        @NonNull
        @Override
        public CarServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_approval_request, parent, false);
            return new CarServiceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CarServiceViewHolder holder, int position) {
            CarService carService = carServiceList.get(position);
            holder.bind(carService);
        }

        @Override
        public int getItemCount() {
            return carServiceList.size();
        }



         class CarServiceViewHolder extends RecyclerView.ViewHolder {
            private ImageView imageView;
            private TextView textServiceName;
            private TextView textLatitude;
            private TextView textLongitude;
            private TextView textPriceList;
            private Button btnApprove;
            private Button btnReject;



            public CarServiceViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.image_car_service);
                textServiceName = itemView.findViewById(R.id.text_service_name);
                textLatitude = itemView.findViewById(R.id.text_latitude);
                textLongitude = itemView.findViewById(R.id.text_longitude);
                textPriceList = itemView.findViewById(R.id.text_price_list);
                btnApprove = itemView.findViewById(R.id.btn_approve);
                btnReject = itemView.findViewById(R.id.btn_reject);
            }

            public void bind(CarService carService) {

                Picasso.get()
                        .load(carService.getImageUrl())
                        .placeholder(R.drawable.baseline_browse_gallery_24)
                        .error(R.drawable.baseline_error_outline_24)
                        .into(imageView);


                textServiceName.setText(carService.getServiceName());
                textLatitude.setText(String.valueOf(carService.getLatitude()));
                textLongitude.setText(String.valueOf(carService.getLongitude()));
                textPriceList.setText(carService.getPriceList());


                btnApprove.setOnClickListener(view -> {
                    approveRequest(carService);
                });

                btnReject.setOnClickListener(view -> {
                    rejectRequest(carService);
                });
            }
            private void approveRequest(CarService carService) {
                String ownerId = "1ekcwcOSV8WttQgaFwCyLpH2Iuj2";

                fStore.collection("approvalRequests")
                        .document(ownerId)
                        .collection("pending")
                        .document(carService.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            // Move to another collection
                            fStore.collection("approvedCarServices")
                                    .document(carService.getId())
                                    .set(carService)
                                    .addOnSuccessListener(aVoid1 -> {
                                        int position = carServiceList.indexOf(carService);
                                        carServiceList.remove(carService);
                                        adapter.notifyItemRemoved(position); // Call notifyItemRemoved on the adapter
                                        Toast.makeText(itemView.getContext(), "Request approved", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(itemView.getContext(), "Error approving request: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        })
                        .addOnFailureListener(e -> Toast.makeText(itemView.getContext(), "Error approving request: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                fStore.collection("registrationRequests")
                        .document(carService.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> Log.d("CarServiceApproval", "Request deleted"))
                        .addOnFailureListener(e -> Log.w("CarServiceApproval", "Error deleting request: " + e.getMessage()));
                }
           }

            private void rejectRequest(CarService carService) {
                String ownerId = "1ekcwcOSV8WttQgaFwCyLpH2Iuj2"; // Replace with actual owner user ID
                FirebaseUser user = fAuth.getCurrentUser();

                fStore.collection("approvalRequests")
                        .document(ownerId)
                        .collection("pending")
                        .document(carService.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            fStore.collection("rejectedCarServices")
                                    .document(carService.getId())
                                    .set(carService)
                                    .addOnSuccessListener(aVoid1 -> {
                                int position = carServiceList.indexOf(carService);
                                carServiceList.remove(carService);
                                notifyItemRemoved(position);
                                Toast.makeText(CarServiceApprovalActivity.this, "Request rejected", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(CarServiceApprovalActivity.this, "Error rejecting request: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        })
                        .addOnFailureListener(e -> Toast.makeText(CarServiceApprovalActivity.this, "Error rejecting request: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                fStore.collection("registrationRequests")
                        .document(carService.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> Log.d("CarServiceApproval", "Request deleted"))
                        .addOnFailureListener(e -> Log.w("CarServiceApproval", "Error deleting request: " + e.getMessage()));

            }

    }

}
