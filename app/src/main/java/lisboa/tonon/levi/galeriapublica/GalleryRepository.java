package lisboa.tonon.levi.galeriapublica;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GalleryRepository {

    Context context;

    public GalleryRepository(Context context) {
        this.context = context;
    }

    public List<ImageData> loadImageData(Integer limit, Integer offSet) throws FileNotFoundException {
        // Cria uma lista para armazenar os dados de imagem que serão retornados.
        List<ImageData> imageDataList = new ArrayList<>();

        // Largura e altura para criação de miniaturas (thumbnails).
        int w = (int) context.getResources().getDimension(R.dimen.im_width);
        int h = (int) context.getResources().getDimension(R.dimen.im_height);

        // Define quais colunas serão recuperadas do MediaStore.
        String[] projection = new String[] {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.SIZE
        };

        // Critérios de seleção e argumentos
        String selection = null;
        String[] selectionArgs = null;

        // Ordenação por data de adição
        String sort = MediaStore.Images.Media.DATE_ADDED;

        // Cursor para buscar sobre a coleção de imagens externas.
        Cursor cursor = null;

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            Bundle queryArgs = new Bundle();
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection);
            queryArgs.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs);

            // Passa a coluna e a direção de ordenação.
            queryArgs.putString(ContentResolver.QUERY_ARG_SORT_COLUMNS, sort);
            queryArgs.putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, ContentResolver.QUERY_SORT_DIRECTION_ASCENDING);

            // Define quantos registros e de onde começar.
            queryArgs.putInt(ContentResolver.QUERY_ARG_LIMIT, limit);
            queryArgs.putInt(ContentResolver.QUERY_ARG_OFFSET, offSet);

            // Executa a query com as configurações definidas.
            cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    queryArgs,
                    null
            );
        } else {
            cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    sort + " ASC + LIMIT " + limit + " OFFSET " + offSet
            );
        }

        // Recupera os índices das colunas para fazer leitura dentro do loop.
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
        int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);

        // Percorre cada linha do cursor e extrai as informações.
        while (cursor.moveToNext()) {
            long id = cursor.getLong(idColumn);
            // Monta a URI final que representa a imagem no MediaStore
            Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            String name = cursor.getString(nameColumn);
            int dateAdded = cursor.getInt(dateAddedColumn);
            int size = cursor.getInt(sizeColumn);

            // Criar miniatura do bitmap.
            Bitmap thumb = Util.getBitmap(context, contentUri, w, h);

            // Cria um objeto ImageData com as informações obtidas e adiciona na lista.
            imageDataList.add(
                    new ImageData(
                            contentUri,
                            thumb,
                            name,
                            new Date(dateAdded * 1000L),
                            size
                    )
            );
        }
        return imageDataList;
    }
}
