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

public class ListViewFragment extends Fragment {

    private MainViewModel mViewModel;
    private View view;

    // Método para criar uma nova instância do fragmento.
    public static ListViewFragment newInstance() {
        return new ListViewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Infla o layout "fragment_list_view" e armazena a View resultante
        view = inflater.inflate(R.layout.fragment_list_view, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtém uma instância do MainViewModel a partir da Activity que hospeda o fragmento.
        mViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        // Cria o adaptador que exibirá os itens na lista, utilizando um comparator para diferenciar efetivamente os itens.
        ListAdapter listAdapter = new ListAdapter(new ImageDataComparator());

        // Obtemos o LiveData do ViewModel.
        LiveData<PagingData<ImageData>> liveData = mViewModel.getPageLv();

        // Observamos o LiveData para reagir sempre que PagingData mudar.
        liveData.observe(getViewLifecycleOwner(), new Observer<PagingData<ImageData>>() {
            @Override
            public void onChanged(PagingData<ImageData> objectPagingData) {
                // Quando há uma nova página de dados, submetemos ao adaptador, vinculando o ciclo de vida ao do fragmento, para que a lista se atualize.
                listAdapter.submitData(getViewLifecycleOwner().getLifecycle(), objectPagingData);
            }
        });

        // Configura o RecyclerView
        RecyclerView rvGallery = (RecyclerView) view.findViewById(R.id.rvList);
        rvGallery.setAdapter(listAdapter);

        // Define o gerenciador de layout como LinearLayoutManager, para exibir os itens de forma linear.
        rvGallery.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
