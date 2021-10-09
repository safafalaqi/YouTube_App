package com.example.youtubeapp
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.youtubeapp.databinding.ItemRowBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer


class RVAdapter(
    private val detail: Array<Array<String>>,
    private val player: YouTubePlayer,
    private val timestamplist: FloatArray
): RecyclerView.Adapter<RVAdapter.ItemViewHolder>() {
    class ItemViewHolder(val binding: ItemRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val title=detail[position][0]
        val id=detail[position][1]
        holder.binding.apply {
            btVideo.text = title
        }
        holder.binding.btVideo.setOnClickListener{
            //here timestamp list will enable the video to start from where we stop last time
            player.loadVideo(id,timestamplist[position])
           // player.pause()
        }
    }

    override fun getItemCount()= detail.size


}