
// 返回值：当两字符串相等时返回0
int strings_not_equal(char *str1, char *str2);

void phase_1(char *input) {
	if (strings_not_equal(input, "Border relations with Canada have never been better.") != 0) {
		explode_bomb();
	}
}

void phase_2(char *input) {
	int i;
	int nums[6];
	read_six_numbers(input, nums);
	if (nums[0] != 1) {
		explode_bomb();
	}
	for (i = 1; i <= 5; ++i) {
		if (nums[i] != nums[i - 1] * 2) {
			explode_bomb();
		}
	}
}

// 返回值：实际读取的个数
// 注意第三个参数是可变参数
int __isoc99_sscanf@plt(char *src, char *format,int *nums[]);

//解析字符串，变成六个数字
void read_six_numbers(char *src, int des[]) {
	int amount = __isoc99_sscanf@plt(src, "%d %d %d %d %d %d"/*0x4025c3*/,
		&des[0], &des[1], &des[2], &des[3], &des[4], &des[5]);
	if (amount <= 5) {
		explode_bomb();
	}
}

// jmp的间接跳转会两次访问内存
void phase_3(char *input) {
	int number1, number2;
	int goal;
	int amount = __isoc99_sscanf@plt(input, "%d %d"/*0x4025cf*/, &number1, &number2);
	if (amount <= 1) {
		explode_bomb();
	}
	if (number1 > 7) {
		explode_bomb();
	}
	switch (number1) {
		case 0:
			goal = 207;
			break;
		...
		// case 1 to case 6
		case 7:
			goal = 327;			
	}
	if (number2 != goal) {
		explode_bomb();
	}
}

void phase_4(char *input) {
	int number1, number2;
	int result;
	int amount = __isoc99_sscanf@plt(input, "%d %d"/*0x4025cf*/, &number1,number2);
	if (amount != 2) {
		explode_bomb();
	}
	if (number1 > 14) {
		explode_bomb();
	}
	result = func4(&number1, 0, 14);
	if (result != 0) {
		explode_bomb();
	}
	if (number2 != 0) {
		explode_bomb();
	}
}

// p1 in edi, p2 in esi, p3 in edx
int func4(int p1,int p2,int p3) {
	// local1 stores in eax
	int local1 = p3 - p2;
	// local2 stores in ecx
	int local2 = local1 >> 31;
	local1 += local2;
	local1 = local1 >> 1;
	local2 = local1 + p2;
	if (local2 > p1) {
		p3 = local2 - 1;
		func4(p1, p2, p3);
		local1 = local1 * 2;
	} else {
		local1 = 0;
		if (local2 < p1) {
			p2 = local2 + 1;
			func4(p1, p2, p3);
			local1 = 2 * local1 + 1;
		}
	}
	return local1;
}

void phase_5(char *input) {
	int i;
	char *someChars = "maduiersnfotvbyl";// stores in 0x4024b0
	char combination[6];
	char *goal = "flyers";// stores in 0x40245e
	if (strlen(input) != 6) {
		explode_bomb();
	}
	for (i = 0; i != 6; ++i) {
		combination[i] = someChars[input[i] & 0xf];
	}
	if (strings_not_equal(combination, goal) != 0) {
		explode_bomb();
	}
}

typedef struct node {
	int data;
	struct node *next;
}node, *list;

// 注意数据大小是int不是long
// 链表的重新排序
void phase_6(char *input) {
	int i;
	int nums[6];
	node *nodeArray[6];
	node *goNode = NULL;
	node *original = 0x6032d0;
	read_six_numbers(input, nums);
	for (i = 0; i != 6; ++i) {
		if (nums[i] - 1 > 5) {//使用减一为了排除0，比较的时候使用无符号比较
			explode_bomb();
		}
		for (j = i; j <= 5; ++j) {
			if (nums[i] == nums[j]) {
				explode_bomb();
			}
		}
	}
	for (i = 0; i != 6; ++i) {
		nums[i] = 7 - nums[i];
	}
	for (i = 0; i <= 5; ++i) {
		goNode = original;
		for (j = 1; j < nums[i]; ++j) {
			goNode = goNode->next;
		}
		nodeArray[i] = goNode;
	}
	for (i = 1; i <= 5; ++i) {
		nodeArray[i - 1]->next = nodeArray[i];
	}
	for (i = 1; i <=5; ++i) {
		if (nodeArray[i] < nodeArray[i - 1]) {
			explode_bomb();
		}
	}
}