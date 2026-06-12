import React from 'react';
import { ResponsiveContainer, AreaChart, Area, CartesianGrid, XAxis, YAxis, Tooltip } from 'recharts';

const AreaDataChart = ({ data = [] }) => {
  return (
    <ResponsiveContainer width="100%" height="100%">
      <AreaChart data={data} margin={{ top: 12, right: 20, left: 0, bottom: 8 }}>
        <defs>
          <linearGradient id="cassetteArea" x1="0" y1="0" x2="0" y2="1">
            <stop offset="5%" stopColor="#94baed" stopOpacity={0.55} />
            <stop offset="95%" stopColor="#6A98D0" stopOpacity={0.08} />
          </linearGradient>
        </defs>
        <CartesianGrid strokeDasharray="3 3" vertical={false} />
        <XAxis dataKey="name" tickLine={false} axisLine={false} />
        <YAxis allowDecimals={false} tickLine={false} axisLine={false} width={28} />
        <Tooltip />
        <Area
          type="monotone"
          dataKey="cassettes"
          stroke="#6A98D0"
          strokeWidth={2}
          fill="url(#cassetteArea)"
          activeDot={{ r: 4 }}
        />
      </AreaChart>
    </ResponsiveContainer>
  );
};

export default AreaDataChart;
