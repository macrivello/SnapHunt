package com.michaelcrivello.apps.snaphunt.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.michaelcrivello.apps.snaphunt.data.model.UserDigest;
import com.michaelcrivello.apps.snaphunt.misc.Selectable;
import com.michaelcrivello.apps.snaphunt.view.UserDigestListItemView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tao on 6/18/15.
 */
public class SelectableUserDigestAdapter extends UserDigestAdapter implements SelectableAdapter {
    HashMap<String, Selectable> selectables;
    ArrayList<Selectable> selected;

    public SelectableUserDigestAdapter(Context context) {
        super(context);
        selectables = new HashMap<String, Selectable>();
        selected = new ArrayList<>();
    }

    @Override
    public List<?> getSelectedItems() {
        return selected;
    }

    @Override
    public Selectable getSelectable(String key) {
        return selectables.get(key);
    }

    @Override
    public void setSelected(Selectable selectable, boolean selectedState) {
        selectable.setSelected(selectedState);
        if (selectedState) {
            selected.add(selectable);
        } else {
            selected.remove(selectable);
        }
        notifyDataSetChanged();
    }

    @Override
    public void loadUsers(List<UserDigest> userDigests) {
        for (UserDigest u : userDigests) {
            selectables.put(u.getId().toHexString(), new Selectable(u));
        }

        super.loadUsers(userDigests);
    }
}
