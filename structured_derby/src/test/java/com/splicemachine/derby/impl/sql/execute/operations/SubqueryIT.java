package com.splicemachine.derby.impl.sql.execute.operations;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.splicemachine.derby.test.framework.SpliceDataWatcher;
import com.splicemachine.derby.test.framework.SpliceSchemaWatcher;
import com.splicemachine.derby.test.framework.SpliceTableWatcher;
import com.splicemachine.derby.test.framework.SpliceWatcher;
import com.splicemachine.homeless.TestUtils;
import org.junit.*;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Scott Fines
 *         Created on: 5/17/13
 */
public class SubqueryIT { 
    private static List<String> t3RowVals = Arrays.asList("('E1','P1',40)",
            "('E1','P2',20)",
            "('E1','P3',80)",
            "('E1','P4',20)",
            "('E1','P5',12)",
            "('E1','P6',12)",
            "('E2','P1',40)",
            "('E2','P2',80)",
            "('E3','P2',20)",
            "('E4','P2',20)",
            "('E4','P4',40)",
            "('E4','P5',80)",
            "('E8','P8',NULL)");

    protected static SpliceWatcher spliceClassWatcher = new SpliceWatcher();

    public static final String CLASS_NAME = SubqueryIT.class.getSimpleName();

    protected static SpliceSchemaWatcher schemaWatcher = new SpliceSchemaWatcher(CLASS_NAME);

    protected static SpliceTableWatcher t1Watcher = new SpliceTableWatcher("t1",schemaWatcher.schemaName,"(k int, l int)");
    protected static SpliceTableWatcher t2Watcher = new SpliceTableWatcher("t2",schemaWatcher.schemaName,"(k int, l int)");
    protected static SpliceTableWatcher t3Watcher = new SpliceTableWatcher("WORKS8",schemaWatcher.schemaName,
            "(EMPNUM VARCHAR(3) NOT NULL, PNUM VARCHAR(3) NOT NULL,HOURS DECIMAL(5))");


    private static final int size = 10;

    @ClassRule
    public static TestRule chain = RuleChain.outerRule(spliceClassWatcher)
            .around(schemaWatcher)
            .around(t1Watcher)
            .around(t2Watcher)
            .around(t3Watcher)
            .around(new SpliceDataWatcher() {
                @Override
                protected void starting(Description description) {
                    try {
                        PreparedStatement ps = spliceClassWatcher.prepareStatement("insert into "+t2Watcher.toString()+" values (?,?)");
                        for(int i=0;i<size;i++){
                            ps.setInt(1,i);
                            ps.setInt(2,i+1);
                            ps.execute();
                        }

                        ps = spliceClassWatcher.prepareStatement("insert into "+ t1Watcher.toString()+" values(?,?)");
                        for(int i=0;i<size;i++){
                            ps.setInt(1,i);
                            ps.setInt(2,i+1);
                            ps.execute();
                            if(i%2==0){
                                //put in a duplicate
                                ps.setInt(1,i);
                                ps.setInt(2,i+1);
                                ps.execute();
                            }

                        }

                        //  load t3
                        for (String rowVal : t3RowVals) {
                            spliceClassWatcher.getStatement().executeUpdate("insert into "+t3Watcher.toString()+" values " + rowVal);
                        }

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }finally{
                        spliceClassWatcher.closeAll();
                    }
                }

            })
            .around(TestUtils.createFileDataWatcher(spliceClassWatcher, "test_data/employee.sql", CLASS_NAME))
            .around(TestUtils.createFileDataWatcher(spliceClassWatcher, "null_int_data.sql", schemaWatcher.schemaName));

    @Rule public SpliceWatcher methodWatcher = new SpliceWatcher();

    @Test
    public void testSubqueryWithSum() throws Exception {
        /* Regression test for Bug 883/884*/
        ResultSet rs = methodWatcher.executeQuery("select k from "+t1Watcher+" where k not in (select sum(k) from "+t2Watcher+")");
        List<Integer> correctResults = Lists.newArrayList();
        for(int i=0;i<size;i++){
            correctResults.add(i);
            if(i%2==0)
                correctResults.add(i);
        }

        int count = 0;
        while(rs.next()){
            count++;
        }

        Assert.assertEquals("Incorrect count returned!",size+size/2,count);
    }

    @Test
    public void testValuesSubSelect() throws Exception {
        /*
         * Regression for Bug 285. Make sure that values ((select ..)) works as expected
         */
        ResultSet rs = methodWatcher.executeQuery("values ((select k from "+t2Watcher.toString()+" where k=1),2)");
        Set<Integer> correctResults = Sets.newHashSet(1);
        List<Integer> retResults = Lists.newArrayList();

        while(rs.next()){
            int val = rs.getInt(1);
            int sec = rs.getInt(2);
            System.out.printf("value=%d,sec=%d%n",val,sec);
            Assert.assertTrue("value "+ val+" is not contained in the correct results!",correctResults.contains(val));
            retResults.add(val);
        }
        Assert.assertEquals("Incorrect number of rows returned!",correctResults.size(),retResults.size());

        for(int correct:retResults){
            int numFound=0;
            for(int ret:retResults){
                if(ret==correct){
                    numFound++;
                    if(numFound>1)
                        Assert.fail("Value "+ ret+" was returned more than once!");
                }
            }
        }
    }

    @Test
    public void testValuesSubselectWithTwoSelects() throws Exception {
        ResultSet rs = methodWatcher.executeQuery("values ((select k from "+t2Watcher.toString()+" where k = 1), (select l from "+t2Watcher.toString()+" where l =4))");
        Set<Integer> correctResults = Sets.newHashSet(1);
        Set<Integer> correctSecondResults = Sets.newHashSet(4);

        List<Integer> retResults = Lists.newArrayList();
        List<Integer> retSecResults = Lists.newArrayList();

        while(rs.next()){
            int val = rs.getInt(1);
            int sec = rs.getInt(2);
            System.out.printf("value=%d,sec=%d%n",val,sec);
            Assert.assertTrue("value "+ val+" is not contained in the correct results!",correctResults.contains(val));
            retResults.add(val);

            Assert.assertTrue("second value "+ sec+" is not contained in the correct results!",correctSecondResults.contains(sec));
        }
        Assert.assertEquals("Incorrect number of rows returned!",correctResults.size(),retResults.size());

        for(int correct:retResults){
            int numFound=0;
            for(int ret:retResults){
                if(ret==correct){
                    numFound++;
                    if(numFound>1)
                        Assert.fail("Value "+ ret+" was returned more than once!");
                }
            }
        }
    }

    @Test
    public void testSubqueryInJoinClause() throws Exception {
        /* Regression test for bug 273 */
        ResultSet rs = methodWatcher.executeQuery("select * from "+t1Watcher.toString()+" a join "+ t2Watcher.toString()+" b on a.k = b.k and a.k in (select k from "+ t1Watcher.toString()+" where a.k ="+t1Watcher.toString()+".k)");
        while(rs.next()){
            System.out.printf("k=%d,l=%d",rs.getInt(1),rs.getInt(2));
        }
    }

    @Test
    public void testInDoesNotReturnDuplicates() throws Exception{
        ResultSet rs = methodWatcher.executeQuery(String.format("select k from %s a where k in (select k from %s )",t2Watcher.toString(),t1Watcher.toString()));
        Set<Integer> priorResults = Sets.newHashSet();
        while(rs.next()){
            Integer nextK = rs.getInt(1);
            System.out.printf("nextK=%d%n",nextK);

            Assert.assertTrue("duplicate result "+ nextK +" returned!",!priorResults.contains(nextK));
            priorResults.add(nextK);
        }
        Assert.assertTrue("No Rows returned!",priorResults.size()>0);
    }

    @Test
    @Ignore("Bugzilla #510 Incorrect results for queries involving not null filters")
    public void testNullSubqueryCompare() throws Exception {
        TestUtils.tableLookupByNumber(spliceClassWatcher);
        ResultSet rs = methodWatcher.executeQuery(
                String.format("SELECT EMPNUM, PNUM FROM %1$s WHERE HOURS > (SELECT W2.HOURS FROM %1$s W2 WHERE W2.EMPNUM = 'E8')",
                        t3Watcher.toString()));
        Assert.assertEquals(0, TestUtils.resultSetToMaps(rs).size());
    }

    @Test
    public void testSubqueryWithAny() throws Exception {
        TestUtils.tableLookupByNumber(spliceClassWatcher);
        ResultSet rs = methodWatcher.executeQuery(
                String.format("select * from %s.z1 t1 where t1.s >= ANY (select t2.b from %s.z2 t2)",schemaWatcher.schemaName,schemaWatcher.schemaName));
        List<Map> results = TestUtils.resultSetToMaps(rs);
        Assert.assertEquals(2, results.size());

        for(Map result : results){
            Assert.assertNotNull("Value for column I should not be null", result.get("I"));
            Assert.assertNotNull("Value for column S should not be null", result.get("S"));
            Assert.assertNotNull("Value for column C should not be null", result.get("C"));
        }
    }

    @Test
    public void testCorrelatedExpressionSubqueryOnlyReturnOneRow() throws Exception {
        ResultSet rs = methodWatcher.executeQuery("select w.empnum from works w where empnum = (select empnum from staff s where w.empnum = s.empnum)");
        // WORKS has 12 rows, each should be returned since STAFF contains one of every EMPNUM
        Assert.assertEquals(12, TestUtils.resultSetToMaps(rs).size());
    }
}
