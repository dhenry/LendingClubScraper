package com.dhenry.lendingclubscraper.app.adapters;

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

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

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
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View newView = convertView;
        ViewHolder holder;
        final int listPosition = position;

        final NoteData currentNote = list.get(listPosition);

        if (null == convertView) {
            holder = new ViewHolder();
            newView = inflater.inflate(R.layout.note_summary_row, null);
            holder.loanGradeLetter = (TextView) newView.findViewById(R.id.loanGradeLetter);
            holder.loanGradeNumber = (TextView) newView.findViewById(R.id.loanGradeNumber);
            holder.loanRate = (TextView) newView.findViewById(R.id.loanRate);
            holder.title = (TextView) newView.findViewById(R.id.title);
            holder.amountFunded = (TextView) newView.findViewById(R.id.amountFunded);
            holder.percentageFunded = (ProgressBar) newView.findViewById(R.id.percentageFunded);
            holder.purchaseLessButton = (Button) newView.findViewById(R.id.subtract25);
            holder.purchaseMoreButton = (Button) newView.findViewById(R.id.add25);
            holder.purchaseAmount = (TextView) newView.findViewById(R.id.purchaseAmount);
            newView.setTag(holder);

        } else {
            holder = (ViewHolder) newView.getTag();
        }

        holder.title.setText(currentNote.getTitle());
        holder.loanRate.setText(percentFormat.format(currentNote.getLoanRate()));

        String loanGrade = currentNote.getLoanGrade();

        int loanGradeColor = getLoanGradeColor(loanGrade);

        holder.loanGradeLetter.setText(String.valueOf(loanGrade.charAt(0)));
        holder.loanGradeLetter.setBackgroundColor(loanGradeColor);
        holder.loanGradeNumber.setText(String.valueOf(loanGrade.charAt(1)));

        ColorFilter filter = new LightingColorFilter( loanGradeColor, loanGradeColor);
        holder.loanGradeNumber.getBackground().setColorFilter(filter);

        holder.amountFunded.setText(currencyFormat.format(currentNote.getLoanAmtRemaining()));

        holder.percentageFunded.setProgress(getPercentFunded(currentNote).intValue());

        holder.purchaseMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.set(listPosition, noteOrderer.investIn(currentNote, LendingClubConstants.TWENTY_FIVE_DOLLARS));
                notifyDataSetChanged();
            }
        });

        holder.purchaseLessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.set(listPosition, noteOrderer.investIn(currentNote, -LendingClubConstants.TWENTY_FIVE_DOLLARS));
                notifyDataSetChanged();
            }
        });

        holder.purchaseAmount.setText(currencyFormat.format(currentNote.getAmountToInvest()));

        togglePurchaseButtons(currentNote, holder);

        return newView;
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
        TextView loanGradeLetter;
        TextView loanGradeNumber;
        TextView loanRate;
        TextView title;
        TextView amountFunded;
        ProgressBar percentageFunded;
        Button purchaseMoreButton;
        Button purchaseLessButton;
        TextView purchaseAmount;
    }

    public void add(NoteData listItem) {
        list.add(listItem);
        notifyDataSetChanged();
    }

    public void removeAllViews() {
        list.clear();
        notifyDataSetChanged();
    }
}
