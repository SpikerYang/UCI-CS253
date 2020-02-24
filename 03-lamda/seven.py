#!/usr/bin/env python
import re, sys, operator
# Mileage may vary. If this crashes, make it lower
RECURSION_LIMIT = 9500
# We add a few more, because, contrary to the name, # this doesn’t just rule recursion: it rules the
# depth of the call stack sys.setrecursionlimit(RECURSION_LIMIT+10)
sys.setrecursionlimit(RECURSION_LIMIT+10)
# File data initialization
stop_words = set(open('../stop_words.txt').read().split(','))
words = re.findall('[a-z]{2,}', open(sys.argv[1]).read().lower())
word_freqs = {}
# Definition of Y combinator
Y = (lambda h: lambda F: F(lambda x: h(h)(F)(x)))(lambda h: lambda F: F(lambda x: h(h)(F)(x)))
# Recursively add word & freq
for i in range(0, len(words), RECURSION_LIMIT):
	Y(lambda f: lambda wList: lambda sWords: lambda wFreqs: 
		None if wList == []
		else f(wList[1: ])(sWords)(wFreqs) if wFreqs.update({wList[0] : wFreqs.get(wList[0], 0) + (1 if  wList[0] not in sWords else 0)}) is None
		else True 
		)(words[i : i + RECURSION_LIMIT])(stop_words)(word_freqs)
#Sort the word_freqs
word_freqs = sorted(word_freqs.items(), key=operator.itemgetter(1), reverse=True)[:25]
#Recursively print word & freq
Y(lambda f: lambda wFreqs: 
	None if wFreqs == []
    else f(wFreqs[1: ]) if print(f"{wFreqs[0][0]}  -  {wFreqs[0][1]}") is None
    else None)(word_freqs)
# def count(word_list, stopwords, wordfreqs): # What to do with an empty list
# if word_list == []:
# return
#     # The inductive case, what to do with a list of words
# else:
# # Process the head word word = word_list[0]
# if word not in stopwords:
# if word in word_freqs: wordfreqs[word] += 1
# else:
# wordfreqs[word] = 1
#         # Process the tail
# count(word_list[1:], stopwords, wordfreqs)


# def wf_print(wordfreq): 
# 	if wordfreq == []:
# return else:
# (w, c) = wordfreq[0] print w, ’-’, c wf_print(wordfreq[1:])

# # Theoretically, we would just call count(words, word_freqs)
# # Try doing that and see what happens.
# for i in range(0, len(words), RECURSION_LIMIT):
# count(words[i:i+RECURSION_LIMIT], stop_words, word_freqs)
# wf_print(sorted(word_freqs.iteritems(), key=operator.itemgetter(1) , reverse=True)[:25])