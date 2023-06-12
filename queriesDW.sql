use dwh;

-- Q1 top 3 stores which generated highest sales in september 2017
SELECT st.store_name,SUM(f.sale)
FROM fact_table f,store_dim st, time_dim t
WHERE f.store_id = st.store_id
AND f.time_id = t.time_id
AND month(t.time) = 9
group by st.store_name
order by SUM(f.sale) desc
limit 3;

-- Q2 top 10 suppliers that generated most revenue over the weekends. explain how we can forecast top suppliers for next weekend.
SELECT st.supplier_name,SUM(f.sale)
FROM fact_table f,supplier_dim st, time_dim t
WHERE f.supplier_id = st.supplier_id
AND f.time_id = t.time_id
AND  dayname(t.time) in ('Saturday','Sunday')
group by st.supplier_name
order by SUM(f.sale) desc
limit 10; 
-- EXPLANATION: We can forecast top suppliers for next week based on pervious trends. 
-- Based on the above query, we can observe that 'A.G. Edwards Inc.' has significantly contributed to sales more than any other
-- The same can be said for 'The AES Corporation' and somewhat for 'CellStar Corp.' as well.

-- Q3 total sales of all products supplied by each supplier with respect to quarter and month
SELECT st.supplier_name,quarter(t.time),monthname(t.time),SUM(f.sale)
FROM fact_table f,supplier_dim st, time_dim t
WHERE f.supplier_id = st.supplier_id
AND f.time_id = t.time_id
group by st.supplier_name,quarter(t.time),month(t.time);

-- Q4 total sales of each product sold by each store. organize reusult store wise, then product wise
SELECT st.store_name,p.prod_name,SUM(f.sale)
FROM fact_table f,store_dim st,prod_dim p
WHERE f.store_id = st.store_id
AND f.prod_id = p.prod_id
group by st.store_name,p.prod_name
order by st.store_name,p.prod_name;

-- Q5 present quaterly sales analysis  for all stores using drill down query concepts
SELECT st.store_name,quarter(t.time),SUM(f.sale)
FROM fact_table f,store_dim st, time_dim t
WHERE f.store_id = st.store_id
AND f.time_id = t.time_id
group by st.store_name,quarter(t.time)
order by st.store_name,quarter(t.time);

-- Q6 top 5 popular products sold over weekend
SELECT p.prod_name,SUM(f.quantity)
FROM fact_table f,prod_dim p, time_dim t
WHERE f.prod_id = p.prod_id
AND f.time_id = t.time_id
AND  dayname(t.time) in ('Saturday','Sunday')
group by p.prod_name
order by SUM(f.quantity) desc
limit 5; 

-- Q7 perform rollup operation for store,supplier,product
SELECT s.store_name,sp.supplier_name,p.prod_name,SUM(f.quantity),sum(f.sale)
FROM fact_table f,prod_dim p,store_dim s,supplier_dim sp
WHERE f.prod_id = p.prod_id
AND s.store_id = f.store_id
AND sp.supplier_id = f.supplier_id
group by s.store_name,sp.supplier_name,p.prod_name with rollup;
-- in a rollup query, we move from more specific to less specific
-- the result shows data:
-- a) grouped on basis of store_name, supplier_name, prod_name
-- b)  grouped on basis of store_name, supplier_name while prod_name fully summed
-- c)  grouped on basis of store_name while supplier_name and prod_name fully summed
-- d)  store_name,supplier_name and prod_name fully summed

-- Q8 extract total sales of each product for first and second half of 2017 along with its total yearly sales
SELECT p.prod_name,ceil(quarter(t.time)/2) as 'first or second half of 2017',t.time,SUM(f.sale)
FROM fact_table f,prod_dim p, time_dim t
WHERE f.prod_id = p.prod_id
AND f.time_id = t.time_id
AND year(t.time) = 2017
group by p.prod_name,ceil(quarter(t.time)/2),t.time with rollup;

-- Q9 find anomaly in DWH dataset. explain in report
SELECT prod_name,count(prod_id)
FROM prod_dim
group by prod_name
order by count(prod_id) desc;
-- as we can see, Tomatoes comes with 2 different product ids

-- Q10 create materialized view for store/product. Also tell how it helps query optimization
drop table if exists `STORE_PRODUCT_ANALYSIS`;

create table STORE_PRODUCT_ANALYSIS AS
Select s.store_name as 'store_name',p.prod_name as 'prod_name',SUM(f.quantity) as 'quanitity',sum(f.sale) as 'sale'
from  fact_table f,prod_dim p, store_dim s
where f.store_id = s.store_id
and f.prod_id = p.prod_id
group by s.store_name,p.prod_name
order by s.store_name,p.prod_name;

select store_name, prod_name, quanitity, sale
from STORE_PRODUCT_ANALYSIS;

-- EXPLANATION: Materialized view helps as instead of doing calculations at run time,
-- we store it in another table
-- As data is preaggregated, it will take very less time to query on it (as we will just have to retrieve it)
-- It will become even more convinient if this query had to be run frequently