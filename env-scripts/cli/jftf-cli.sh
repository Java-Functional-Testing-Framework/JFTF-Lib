#!/bin/bash

jftf_test_dir=~/.jftf/test_cases

usage() {
  echo "Usage: $0 [-g <testGroup>] [-p <testCase>] [-s testSuite <1|0>]" 1>&2; exit 1;
}

execute_test_case(){
  echo "JFTF-CLI ---> Executing test case ${g}/${p}!"
  jftf_test_case=${jftf_test_dir}/${g}/${p}/bin/${p}
  if [ -f "${jftf_test_case}" ]; then
    echo "JFTF-CLI ---> Found test case!";
    echo "JFTF-CLI ---> Test full path: '${jftf_test_case}'";
  else
    echo "JFTF-CLI ---> Test case not found!"
    exit 1;
  fi
  echo
  echo
  echo
  run_Command=bash "${jftf_test_case}" -d JftfDetachedRunner
  test_Output=${run_Command}
  echo "JFTF-CLI ---> Test case execution finished!"
  exit 0;
}

while getopts ":g:p:s:" o; do
    case "${o}" in
        g)
            g=${OPTARG}
            ;;
        p)
            p=${OPTARG}
            ;;
        s)
            s=${OPTARG}
            ((s == 1 || s == 0)) || usage
            ;;
        *)
            usage
            ;;
    esac
done
shift $((OPTIND-1))

if [ "$s" == "0" ]; then
  if [ -z "${g}" ] || [ -z "${p}" ] || [ -z "${s}" ]; then
      usage
  else
    execute_test_case
  fi
elif [ "$s" == "1" ]; then
  if [ -z "${g}" ]; then
    usage
  fi
fi

