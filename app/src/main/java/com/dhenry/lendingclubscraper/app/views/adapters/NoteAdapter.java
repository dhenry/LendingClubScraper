package com.dhenry.lendingclubscraper.app.views.adapters;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dhenry.lendingclubscraper.app.R;
import com.dhenry.lendingclubscraper.app.constants.LendingClubConstants;
import com.dhenry.lendingclubscraper.app.persistence.models.NoteData;
import com.dhenry.lendingclubscraper.app.utilities.NoteOrderer;
import com.dhenry.lendingclubscraper.app.utilities.NumberFormats;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NoteAdapter extends BaseAdapter {

    private Context mContext;
    private NoteOrderer noteOrderer;
    private static LayoutInflater inflater = null;
    private ArrayList<NoteData> list = new ArrayList<NoteData>();

    private static DecimalFormat percentFormat;
    private static NumberFormat currencyFormat = NumberFormats.CURRENCY_FORMAT;

    {
        percentFormat = (DecimalFormat) NumberFormats.PERCENT_FORMAT;
        percentFormat.setMultiplier(1);
    }

    public NoteAdapter(Context context, NoteOrderer notePurchaser) {
        this.mContext = context;
        this.noteOrderer = notePurchaser;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public NoteData getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.note_summary_row, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        final NoteData currentNote = getItem(position);

        holder.title.setText(currentNote.getTitle());
        holder.loanRate.setText(percentFormat.format(currentNote.getLoanRate()));

        final String loanGrade = currentNote.getLoanGrade();
        final int loanGradeColor = getLoanGradeColor(loanGrade);

        holder.loanGradeLetter.setText(String.valueOf(loanGrade.charAt(0)));
        holder.loanGradeLetter.setBackgroundColor(loanGradeColor);
        holder.loanGradeNumber.setText(String.valueOf(loanGrade.charAt(1)));

        ColorFilter filter = new LightingColorFilter( loanGradeColor, loanGradeColor);
        holder.loanGradeNumber.getBackground().setColorFilter(filter);

        holder.amountFunded.setText(currencyFormat.format(currentNote.getLoanAmtRemaining()));

        holder.percentageFunded.setProgress(getPercentFunded(currentNote).intValue());

        final int listPos = position;

        holder.purchaseMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.set(listPos, noteOrderer.investIn(currentNote, LendingClubConstants.TWENTY_FIVE_DOLLARS));
                notifyDataSetChanged();
            }
        });

        holder.purchaseLessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.set(listPos, noteOrderer.investIn(currentNote, -LendingClubConstants.TWENTY_FIVE_DOLLARS));
                notifyDataSetChanged();
            }
        });

        holder.purchaseAmount.setText(currencyFormat.format(currentNote.getAmountToInvest()));

        togglePurchaseButtons(currentNote, holder);

        return view;
    }

    private void togglePurchaseButtons(NoteData noteData, ViewHolder holder)
    {
        if (noteData.getAmountToInvest().equals(0)) {
            holder.purchaseLessButton.setEnabled(false);
        } else {
            holder.purchaseLessButton.setEnabled(true);
        }
    }

    private int getLoanGradeColor(String loanGrade) {
        switch (loanGrade.charAt(0)) {
            case 'A':
                return mContext.getResources().getColor(R.color.gradeA);
            case 'B':
                return mContext.getResources().getColor(R.color.gradeB);
            case 'C':
                return mContext.getResources().getColor(R.color.gradeC);
            case 'D':
                return mContext.getResources().getColor(R.color.gradeD);
            case 'E':
                return mContext.getResources().getColor(R.color.gradeE);
            case 'F':
                return mContext.getResources().getColor(R.color.gradeF);
            case 'G':
                return mContext.getResources().getColor(R.color.gradeG);
            default:
                return mContext.getResources().getColor(R.color.gradeG);
        }
    }

    private Double getPercentFunded(NoteData currentNote) {
        return 100 - (100 / (currentNote.getLoanAmt() / currentNote.getLoanAmtRemaining()));

    }

    static class ViewHolder {
        @InjectView(R.id.loanGradeLetter) TextView loanGradeLetter;
        @InjectView(R.id.loanGradeNumber) TextView loanGradeNumber;
        @InjectView(R.id.loanRate) TextView loanRate;
        @InjectView(R.id.title) TextView title;
        @InjectView(R.id.amountFunded) TextView amountFunded;
        @InjectView(R.id.percentageFunded) ProgressBar percentageFunded;
        @InjectView(R.id.subtract25) Button purchaseMoreButton;
        @InjectView(R.id.add25) Button purchaseLessButton;
        @InjectView(R.id.purchaseAmount) TextView purchaseAmount;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public void add(NoteData listItem) {
        list.add(listItem);
        notifyDataSetChanged();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }
}
