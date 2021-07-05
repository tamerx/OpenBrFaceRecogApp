import pandas as pd

dfObj = pd.read_csv("match_scores.csv", delimiter=",")
dfObj = dfObj.iloc[:, 1:]
maxValuesObj = dfObj.max(skipna=False)
max_key, max_value = maxValuesObj[maxValuesObj == maxValuesObj.max()].index[0], maxValuesObj.max()
delimiter=";;;"
max_str = str(max_key) + delimiter + str(max_value)
print(max_str)