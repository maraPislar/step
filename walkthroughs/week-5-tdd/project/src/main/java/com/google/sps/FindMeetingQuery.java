// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> times = new ArrayList();

    if (request.getDuration() >= TimeRange.WHOLE_DAY.duration() + 1) {
      return times;
    }

    if (events.isEmpty()){
      times.add(TimeRange.WHOLE_DAY);
      return times;
    }

    ArrayList<Event> e = new ArrayList<>(events);

    if(e.get(0).getWhen().start() != TimeRange.START_OF_DAY) {
      times.add(TimeRange.fromStartEnd(
          TimeRange.START_OF_DAY, e.get(0).getWhen().start(), false
      ));
    }

    for (int i = 0; i < e.size() - 1; i++) {
      if (e.get(i).getWhen().overlaps(e.get(i + 1).getWhen())) {
        continue;
      } else {
        if (e.get(i + 1).getWhen().start() - e.get(i).getWhen().end() >= request.getDuration()) {
          times.add(TimeRange.fromStartEnd(
            e.get(i).getWhen().end(), e.get(i + 1).getWhen().start(), false
        ));
        }
      }
    }

    if(e.get(e.size() - 1).getWhen().end() != 1440) {
      times.add(TimeRange.fromStartEnd(
          e.get(e.size() - 1).getWhen().end(), TimeRange.END_OF_DAY, true
      ));
    }

    return times;
  }
}
