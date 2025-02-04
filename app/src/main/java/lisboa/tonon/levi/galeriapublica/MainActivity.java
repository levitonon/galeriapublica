package lisboa.tonon.levi.galeriapublica;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import android.Manifest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // Define o fragmento que será mostrado.
        fragmentTransaction.replace(R.id.fragContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Recupera uma instância do MainViewModel, que gerencia dados e estado para esta Activity.
        final MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);

        // Localiza o BottomNavigationView no layout.
        bottomNavigationView = findViewById(R.id.btNav);

        // Configura o item selecionado da barra de navegação.
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Sinaliza ao ViewModel qual item de navegação foi escolhido.
                vm.setNavigationOpSelected(item.getItemId());

                int itemId = item.getItemId();
                // Se for selecionada a opção de “grid” (R.id.gridViewOp), cria e exibe o GridViewFragment.
                if (itemId == R.id.gridViewOp) {
                    GridViewFragment gridViewFragment = GridViewFragment.newInstance();
                    setFragment(gridViewFragment);
                }
                // Se for selecionada a opção de “lista” (R.id.listViewOp), cria e exibe o ListViewFragment.
                else if (itemId == R.id.listViewOp) {
                    ListViewFragment listViewFragment = ListViewFragment.newInstance();
                    setFragment(listViewFragment);
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Lista de permissões para verificar
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        // Método chamado para ver se as permissões necessárias já foram concedidas.
        checkForPermissions(permissions);
    }

    private void checkForPermissions(List<String> permissions) {
        // Monta uma lista para armazenar as permissões não concedidas.
        List<String> permissionsNotGranted = new ArrayList<>();
        permissionsNotGranted.size();

        // Recupera o ViewModel para saber qual item de navegação está selecionado.
        MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);
        int navigationOpSelected = vm.getNavigationOpSelected();

        // Força o BottomNavigationView a marcar o item que estava selecionado anteriormente.
        bottomNavigationView.setSelectedItemId(navigationOpSelected);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        // Chamado quando o usuário responde ao pedido de permissões.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Lista de permissões rejeitadas.
        List<String> permissionsRejected = new ArrayList<>();

        // Itera sobre o array de permissões para ver quais foram negadas.
        for (int i = 0; i < permissions.length; i++) {
            // Se o resultado não foi PERMISSION_GRANTED, adiciona à lista de rejeitadas.
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                permissionsRejected.add(permissions[i]);
            }
        }

        if (!permissionsRejected.isEmpty()) {
            // Exibir alerta
        } else {
            // Se todas as permissões foram concedidas, restaura o item de navegação selecionado.
            MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);
            int navigationOpSelected = vm.getNavigationOpSelected();
            bottomNavigationView.setSelectedItemId(navigationOpSelected);
        }
    }
}
