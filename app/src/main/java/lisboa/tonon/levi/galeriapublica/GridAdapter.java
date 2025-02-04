package lisboa.tonon.levi.galeriapublica;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;

import java.text.SimpleDateFormat;

public class GridAdapter extends PagingDataAdapter<ImageData, MyViewHolder> {

    public GridAdapter(@NonNull DiffUtil.ItemCallback<ImageData> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout 'list_item' para cada item do RecyclerView.
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_item, parent, false);
        // Retorna uma instância de MyViewHolder com a referência ao layout inflado.
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Obtém o objeto ImageData correspondente à posição atual.
        ImageData imageData = getItem(position);

        // Preenche o TextView com o nome do arquivo de imagem.
        TextView tvName = holder.itemView.findViewById(R.id.tvName);
        tvName.setText(imageData.fileName);

        // Formata a data no padrão "HH:mm dd/MM/yyyy" e atribui ao TextView.
        TextView tvDate = holder.itemView.findViewById(R.id.tvDate);
        tvDate.setText("Data: " + new SimpleDateFormat("HH:mm dd/MM/yyyy").format(imageData.date));

        // Exibe o tamanho do arquivo de imagem.
        TextView tvSize = holder.itemView.findViewById(R.id.tvSize);
        tvSize.setText("Tamanho: " + String.valueOf(imageData.size));

        // Carrega a miniatura (bitmap) armazenada em imageData.
        Bitmap thumb = imageData.thumb;
        // Atribui essa miniatura ao ImageView para exibição.
        ImageView imageView = holder.itemView.findViewById(R.id.imThumb);
        imageView.setImageBitmap(thumb);
    }
}
