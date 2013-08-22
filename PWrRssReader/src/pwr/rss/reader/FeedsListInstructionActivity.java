package pwr.rss.reader;

public class FeedsListInstructionActivity extends InstructionActivity {
	
	@Override
	protected int getFirstLayout() {
		return R.layout.activity_instruction_list_first;
	}
	
	@Override
	protected int getSecondLayout() {
		return R.layout.activity_instruction_list_second;
	}
}