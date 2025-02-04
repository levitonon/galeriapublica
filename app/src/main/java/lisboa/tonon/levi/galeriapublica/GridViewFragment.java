package lisboa.tonon.levi.galeriapublica;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagingData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GridViewFragment extends Fragment {

    private MainViewModel mViewModel;
    private View view;

    // Criação de instância do fragmento
    public static GridViewFragment newInstance() {
        return new GridViewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Infla o layout "fragment_grid_view" e retorna a view correspondente.
        view = inflater.inflate(R.layout.fragment_grid_view, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtém o ViewModel
        mViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        // Cria uma instância do adaptador
        GridAdapter gridAdapter = new GridAdapter(new ImageDataComparator());

        // Recupera o LiveData contendo o PagingData de ImageData.
        LiveData<PagingData<ImageData>> liveData = mViewModel.getPageLv();

        // Observa as alterações no LiveData. Sempre que o PagingData mudar, o adaptador é atualizado.
        liveData.observe(getViewLifecycleOwner(), new Observer<PagingData<ImageData>>() {
            @Override
            public void onChanged(PagingData<ImageData> objectPagingData) {
                // Submete o novo PagingData ao adaptador, vinculando ao ciclo de vida do fragmento.
                gridAdapter.submitData(getViewLifecycleOwner().getLifecycle(), objectPagingData);
            }
        });

        // Localiza o RecyclerView no layout e configura o adaptador.
        RecyclerView rvGallery = (RecyclerView) view.findViewById(R.id.rvGrid);
        rvGallery.setAdapter(gridAdapter);

        // Define o gerenciador de layout como LinearLayoutManager, exibindo os itens em lista vertical.
        rvGallery.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
