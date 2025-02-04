package lisboa.tonon.levi.galeriapublica;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import kotlinx.coroutines.CoroutineScope;

public class MainViewModel extends AndroidViewModel {

    // Guarda o item de navegação selecionado
    int navigationOpSelected = R.id.gridViewOp;

    // LiveData que emite PagingData contendo objetos ImageData
    LiveData<PagingData<ImageData>> pageLv;

    public MainViewModel(@NonNull Application application) {
        super(application);
        GalleryRepository galleryRepository = new GalleryRepository(application);

        // Iinforma qual parte dos dados deve ser carregada em cada requisição.
        GalleryPagingSource galleryPagingSource = new GalleryPagingSource(galleryRepository);

        // Cria um Pager, definindo uma configuração de paginação
        Pager<Integer, ImageData> pager = new Pager<>(new PagingConfig(10), () -> galleryPagingSource);
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);

        pageLv = PagingLiveData.cachedIn(
                PagingLiveData.getLiveData(pager),
                viewModelScope
        );
    }

    // Retorna o LiveData que emite o PagingData de ImageData.
    public LiveData<PagingData<ImageData>> getPageLv() {
        return pageLv;
    }

    // Informa qual item de navegação está selecionado
    public int getNavigationOpSelected() {
        return navigationOpSelected;
    }

    // Atualiza o estado de qual item de navegação está selecionado.
    public void setNavigationOpSelected(int navigationOpSelected) {
        this.navigationOpSelected = navigationOpSelected;
    }
}
