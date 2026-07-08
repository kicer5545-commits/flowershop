<template>
    <div class="dashboard">
        <div class="chart-container">
            <div class="chart-card">
                <h3>商品分类统计</h3>
                <div ref="categoryChart" style="width: 100%; height: 400px;"></div>
            </div>
            <div class="chart-card">
                <h3>近7天订单趋势</h3>
                <div ref="trendChart" style="width: 100%; height: 400px;"></div>
            </div>
        </div>
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import * as echarts from 'echarts'
    import axiosInstance from '../axios'

    const categoryChart = ref(null)
    const trendChart = ref(null)

    const loadCategoryData = async () => {
        try {
            const data = await axiosInstance.get('/admin/statistics/categoryCount')
            const categories = Object.keys(data)
            const counts = Object.values(data)

            const chart = echarts.init(categoryChart.value)
            chart.setOption({
                tooltip: { trigger: 'item' },
                legend: { top: '5%', left: 'center' },
                series: [{
                    type: 'pie',
                    radius: '55%',
                    data: categories.map((name, i) => ({ name, value: counts[i] })),
                    emphasis: { scale: true },
                    label: { show: true, formatter: '{b}: {d}%' }
                }]
            })
        } catch (error) {
            console.error('加载分类统计失败', error)
        }
    }

    const loadTrendData = async () => {
        try {
            const { dates, counts } = await axiosInstance.get('/admin/statistics/orderTrend')
            const chart = echarts.init(trendChart.value)
            chart.setOption({
                tooltip: { trigger: 'axis' },
                xAxis: { type: 'category', data: dates, name: '日期' },
                yAxis: { type: 'value', name: '订单数量' },
                series: [{
                    data: counts,
                    type: 'line',
                    smooth: true,
                    areaStyle: { opacity: 0.3 },
                    lineStyle: { color: '#ff9494', width: 3 },
                    symbol: 'circle',
                    symbolSize: 8
                }]
            })
        } catch (error) {
            console.error('加载订单趋势失败', error)
        }
    }

    onMounted(() => {
        loadCategoryData()
        loadTrendData()
    })
</script>

<style scoped>
    .dashboard {
        background: white;
        padding: 20px;
        border-radius: 8px;
    }
    .chart-container {
        display: flex;
        flex-wrap: wrap;
        gap: 24px;
    }
    .chart-card {
        flex: 1;
        min-width: 400px;
        background: #fafafa;
        padding: 16px;
        border-radius: 8px;
        box-shadow: 0 1px 4px rgba(0,0,0,0.1);
    }
    .chart-card h3 {
        margin-bottom: 16px;
        text-align: center;
        color: #333;
    }
</style>