package net.osmand.plus.quickaction;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.osmand.plus.R;
import net.osmand.plus.activities.MapActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class SwitchableAction<T> extends QuickAction {

	protected static final String KEY_DIALOG = "dialog";
	private transient EditText title;

	protected SwitchableAction(int type) {
		super(type);
	}

	public SwitchableAction(QuickAction quickAction) {
		super(quickAction);
	}

	@Override
	public void setAutoGeneratedTitle(EditText title) {
		this.title = title;
	}

	@Override
	public void drawUI(ViewGroup parent, final MapActivity activity) {

		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.quick_action_switchable_action, parent, false);
		
		SwitchCompat showDialog = (SwitchCompat) view.findViewById(R.id.saveButton);
		if (!getParams().isEmpty()) {
			showDialog.setChecked(Boolean.valueOf(getParams().get(KEY_DIALOG)));
		}
		
		final RecyclerView list = (RecyclerView) view.findViewById(R.id.list);

		final QuickActionItemTouchHelperCallback touchHelperCallback = new QuickActionItemTouchHelperCallback();
		final ItemTouchHelper touchHelper = new ItemTouchHelper(touchHelperCallback);

		final Adapter adapter = new Adapter(new QuickActionListFragment.OnStartDragListener() {
			@Override
			public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
				touchHelper.startDrag(viewHolder);
			}
		});

		touchHelperCallback.setItemMoveCallback(adapter);
		touchHelper.attachToRecyclerView(list);

		if (!getParams().isEmpty()) {
			adapter.addItems(loadListFromParams());
		}

		list.setAdapter(adapter);

		TextView dscrTitle = (TextView) view.findViewById(R.id.textDscrTitle);
		TextView dscrHint = (TextView) view.findViewById(R.id.textDscrHint);
		Button addBtn = (Button) view.findViewById(R.id.btnAdd);

		dscrTitle.setText(parent.getContext().getString(getDiscrTitle()) + ":");
		dscrHint.setText(getDiscrHint());
		addBtn.setText(getAddBtnText());
		addBtn.setOnClickListener(getOnAddBtnClickListener(activity, adapter));

		parent.addView(view);
	}

	@Override
	public boolean fillParams(View root, MapActivity activity) {

		final RecyclerView list = (RecyclerView) root.findViewById(R.id.list);
		final Adapter adapter = (Adapter) list.getAdapter();

		boolean hasParams = adapter.itemsList != null && !adapter.itemsList.isEmpty();

		if (hasParams) saveListToParams(adapter.itemsList);

		return hasParams;
	}

	protected class Adapter extends RecyclerView.Adapter<Adapter.ItemHolder> implements QuickActionItemTouchHelperCallback.OnItemMoveCallback {

		private List<T> itemsList = new ArrayList<>();
		private final QuickActionListFragment.OnStartDragListener onStartDragListener;

		public Adapter(QuickActionListFragment.OnStartDragListener onStartDragListener) {
			this.onStartDragListener = onStartDragListener;
			this.itemsList = new ArrayList<>();
		}

		@Override
		public Adapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			return new Adapter.ItemHolder(inflater.inflate(R.layout.quick_action_switchable_item, parent, false));
		}

		@Override
		public void onBindViewHolder(final Adapter.ItemHolder holder, final int position) {
			final T item = itemsList.get(position);

			holder.title.setText(getItemName(item));

			holder.handleView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (MotionEventCompat.getActionMasked(event) ==
							MotionEvent.ACTION_DOWN) {
						onStartDragListener.onStartDrag(holder);
					}
					return false;
				}
			});

			holder.closeBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					String oldTitle = getTitle(itemsList);
					String defaultName = holder.handleView.getContext().getString(getNameRes());

					deleteItem(holder.getAdapterPosition());

					if (oldTitle.equals(title.getText().toString()) || title.getText().toString().equals(defaultName)) {

						String newTitle = getTitle(itemsList);
						title.setText(newTitle);
					}
				}
			});
		}

		@Override
		public int getItemCount() {
			return itemsList.size();
		}

		public void deleteItem(int position) {

			if (position == -1) {
				return;
			}

			itemsList.remove(position);
			notifyItemRemoved(position);
		}

		public void addItems(List<T> data) {

			if (!itemsList.containsAll(data)) {

				itemsList.addAll(data);
				notifyDataSetChanged();
			}
		}

		public void addItem(T item, Context context) {

			if (!itemsList.contains(item)) {

				String oldTitle = getTitle(itemsList);
				String defaultName = context.getString(getNameRes());

				int oldSize = itemsList.size();
				itemsList.add(item);

				notifyItemRangeInserted(oldSize, itemsList.size() - oldSize);

				if (oldTitle.equals(title.getText().toString()) || title.getText().toString().equals(defaultName)) {

					String newTitle = getTitle(itemsList);
					title.setText(newTitle);
				}
			}
		}

		@Override
		public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

			int selectedPosition = viewHolder.getAdapterPosition();
			int targetPosition = target.getAdapterPosition();

			if (selectedPosition < 0 || targetPosition < 0) {
				return false;
			}

			String oldTitle = getTitle(itemsList);
			String defaultName = recyclerView.getContext().getString(getNameRes());

			Collections.swap(itemsList, selectedPosition, targetPosition);
			if (selectedPosition - targetPosition < -1) {

				notifyItemMoved(selectedPosition, targetPosition);
				notifyItemMoved(targetPosition - 1, selectedPosition);

			} else if (selectedPosition - targetPosition > 1) {

				notifyItemMoved(selectedPosition, targetPosition);
				notifyItemMoved(targetPosition + 1, selectedPosition);

			} else {

				notifyItemMoved(selectedPosition, targetPosition);
			}

			notifyItemChanged(selectedPosition);
			notifyItemChanged(targetPosition);

			if (oldTitle.equals(title.getText().toString()) || title.getText().toString().equals(defaultName)) {

				String newTitle = getTitle(itemsList);
				title.setText(newTitle);
			}

			return true;
		}

		@Override
		public void onViewDropped(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
		}

		public class ItemHolder extends RecyclerView.ViewHolder {
			public TextView title;
			public ImageView handleView;
			public ImageView closeBtn;

			public ItemHolder(View itemView) {
				super(itemView);

				title = (TextView) itemView.findViewById(R.id.title);
				handleView = (ImageView) itemView.findViewById(R.id.handle_view);
				closeBtn = (ImageView) itemView.findViewById(R.id.closeImageButton);
			}
		}
	}

	protected abstract String getTitle(List<T> filters);

	protected abstract void saveListToParams(List<T> list);

	protected abstract List<T> loadListFromParams();

	protected abstract String getItemName(T item);

	protected abstract
	@StringRes
	int getAddBtnText();

	protected abstract
	@StringRes
	int getDiscrHint();

	protected abstract
	@StringRes
	int getDiscrTitle();

	protected abstract String getListKey();

	protected abstract View.OnClickListener getOnAddBtnClickListener(MapActivity activity, final Adapter adapter);
}
