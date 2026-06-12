import React from 'react';
import { Pie, PieChart, Sector, ResponsiveContainer, Tooltip } from 'recharts';

const RADIAN = Math.PI / 180;
const COLORS = ['#0088FE', '#00C49F', '#bb28ff', '#7b42ff'];

// Removed the : PieLabelRenderProps type annotation
const renderCustomizedLabel1 = ({ cx, cy, midAngle, innerRadius, outerRadius, percent }) => {
  if (cx == null || cy == null || innerRadius == null || outerRadius == null) {
    return null;
  }
  const radius = innerRadius + (outerRadius - innerRadius) * 0.5;
  const ncx = Number(cx);
  const x = ncx + radius * Math.cos(-(midAngle ?? 0) * RADIAN);
  const ncy = Number(cy);
  const y = ncy + radius * Math.sin(-(midAngle ?? 0) * RADIAN);

  return (
    <text x={x} y={y} fill="white" textAnchor={x > ncx ? 'start' : 'end'} dominantBaseline="central">
      {`${((percent ?? 1) * 100).toFixed(0)}%`}
    </text>
  );
};

const renderCustomizedLabel = ({ cx, cy, midAngle, outerRadius, percent, name }) => {
  // 1. Position the label outside (1.2 means 20% further out than the pie edge)
  const radius = outerRadius * 1.05; 
  
  const RADIAN = Math.PI / 180;
  const x = cx + radius * Math.cos(-midAngle * RADIAN);
  const y = cy + radius * Math.sin(-midAngle * RADIAN);

  return (
    <text 
      x={x} 
      y={y} 
      fill="#333" // Dark text since it's now on the white background
      textAnchor={x > cx ? 'start' : 'end'} // Aligns text away from the center
      dominantBaseline="central"
      fontSize="12"
      fontWeight="bold"
    >
      {`${name}: ${(percent * 100).toFixed(0)}%`}
    </text>
  );
};

// Removed : PieSectorShapeProps
// const MyCustomPie = (props) => {
//   return <Sector {...props} fill={COLORS[props.index % COLORS.length]} />;
// };

// Changed to a standard JS function with default parameters
export default function PieChartWithCustomizedLabel({ isAnimationActive = true, data }) {
  return (
    /* Tip: Added ResponsiveContainer so it fits your dashboard better */
    <ResponsiveContainer width="100%" height={350}>
      <PieChart>
        <Pie
          data={data}
          labelLine={false}
          label={renderCustomizedLabel}
          fill="rgb(106, 152, 208)"
          dataKey="value"
          nameKey="name"
          isAnimationActive={isAnimationActive}
          responseive={true}
        />
        <Tooltip defaultIndex={0} />
      </PieChart>
    </ResponsiveContainer>
  );
}