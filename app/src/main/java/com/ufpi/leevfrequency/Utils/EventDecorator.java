package com.ufpi.leevfrequency.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import com.ufpi.leevfrequency.R;

import java.util.Collection;
import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {

    private final Context context;
    private final HashSet<CalendarDay> dates;

    public EventDecorator(Context context, Collection<CalendarDay> dates) {
        this.context = context;
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        //view.addSpan(new DotSpan(5, Color.BLACK));
        //view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.calendar_dot));
        //view.setSelectionDrawable(context.getResources().getDrawable(R.drawable.calendar_dot));
    }
}
