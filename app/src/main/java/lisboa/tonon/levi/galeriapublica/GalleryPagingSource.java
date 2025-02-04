package lisboa.tonon.levi.galeriapublica;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.ListenableFuturePagingSource;
import androidx.paging.PagingState;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class GalleryPagingSource extends ListenableFuturePagingSource<Integer, ImageData> {

    GalleryRepository galleryRepository;
    // Repositório que busca as imagens

    Integer initialLoadSize = 0;

    public GalleryPagingSource(GalleryRepository galleryRepository) {
        this.galleryRepository = galleryRepository;
    }

    @Nullable
    @Override
//Determina a chave quando a listagem é inválida
    public Integer getRefreshKey(@NonNull PagingState<Integer, ImageData> pagingState) {
        return null;
    }

    @NonNull
    @Override
    public ListenableFuture<LoadResult<Integer, ImageData>> loadFuture(@NonNull LoadParams<Integer> loadParams) {

        Integer nextPageNumber = loadParams.getKey();
        // Carrega a página atual
        if (nextPageNumber == null) {
            nextPageNumber = 1;
            initialLoadSize = loadParams.getLoadSize();
        }

        Integer offSet = 0;
        if(nextPageNumber == 2) {
            offSet = initialLoadSize;
        } else {
            // Se não for a segunda página, o offset é calculado com base nas páginas anteriores.
            offSet = ((nextPageNumber - 1) * loadParams.getLoadSize())
                    + (initialLoadSize - loadParams.getLoadSize());
        }

        // Executor para a busca de forma assíncrona.
        ListeningExecutorService service =
                MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());

        Integer finalOffSet = offSet;
        Integer finalNextPageNumber = nextPageNumber;

        ListenableFuture<LoadResult<Integer, ImageData>> lf =
                service.submit(new Callable<LoadResult<Integer, ImageData>>() {
                    @Override
                    public LoadResult<Integer, ImageData> call() {
                        // ERealiza a consulta das imagens
                        List<ImageData> imageDataList = null;
                        try {
                            // Busca uma lista de imagem a partir do repositório,
                            imageDataList = galleryRepository.loadImageData(
                                    loadParams.getLoadSize(),
                                    finalOffSet
                            );

                            Integer nextKey = null;
                            // Se ainda há dados suficientes, incrementa a página para continuar.
                            if(imageDataList.size() >= loadParams.getLoadSize()) {
                                nextKey = finalNextPageNumber + 1;
                            }

                            // Retorna uma página de resultado, contendo a lista atual
                            return new LoadResult.Page<>(
                                    imageDataList,
                                    null,
                                    nextKey
                            );
                        } catch (FileNotFoundException e) {
                            // Se ocorrer um erro, retorna um LoadResult.Error.
                            return new LoadResult.Error<>(e);
                        }
                    }
                });

        // Devolve o ListenableFuture, que quando concluído, resultará em um LoadResult para a Paging Library.
        return lf;
    }
}
